package com.example.atm.server.impl;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.util.IsoUtil;
import com.example.atm.server.conn.ConnectionManager;
import com.example.atm.server.event.EventSender;
import com.example.atm.server.jms.ReplyToHolder;
import com.example.atm.server.netty.AtmServerListener;
import com.example.atm.server.registry.AtmRegistry;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;

import static com.example.atm.netty.codec.util.IsoUtil.dump;

@Component
public class AtmMessageListener implements AtmServerListener {
    private static final Logger LOG = LoggerFactory.getLogger(AtmMessageListener.class);

    private static final int MIN_MESSAGE_LENGTH = 20;
    private static final String OFFERING_MSG_TYPE = "9380";

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ReplyToHolder replyToHolder;

    @Autowired
    private AtmRegistry registry;

    @Autowired
    private EventSender eventSender;

    @Value("#{atmServerProperties.switchQueueName}")
    private String switchQueue;

    @Value("#{atmServerProperties.offeringQueueName}")
    private String offeringQueue;

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        LOG.info("{} connected", ctx);
        connectionManager.add(ctx);

        ConnectionEvent event = new ConnectionEvent();
        event.setEventType("CONNECT");
        event.setInfo(ctx.toString());
        eventSender.send(event);
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        LOG.info("{} disconnected", ctx);
        connectionManager.remove(ctx);

        ConnectionEvent event = new ConnectionEvent();
        event.setEventType("DISCONNECT");
        event.setInfo(ctx.toString());
        eventSender.send(event);
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, AtmMessage msg) {

        //Verifica se tem no minimo o codigo da mensagem + mapa de bits
        if (msg.getBody().length() < MIN_MESSAGE_LENGTH) {
            LOG.warn("Invalid Msg Received <{}>", msg);
            ctx.disconnect();
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Received from ATM {}: {}", msg.getId(), dump(msg.getBody()));
        }

        if (isMessageValid(msg)) {
            dispatch(ctx, msg);
        } else {
            LOG.warn("Id not registered. Discarding {}", msg);
            ctx.disconnect();
        }
    }

    private boolean isMessageValid(AtmMessage msg) {
        return registry.isRegistered(msg.getId());
    }

    private void dispatch(ChannelHandlerContext ctx, AtmMessage msg) {
        Long id = msg.getId();

        connectionManager.add(id, ctx);

        String body = msg.getBody();
        String type = body.substring(0, 4);

        String queueName = resolveQueueName(type);

        jmsTemplate.send(queueName, session -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending to {}: {}", queueName, dump(body));
            }

            BytesMessage message = session.createBytesMessage();
            message.setJMSReplyTo(replyToHolder.getReplyToQueue());
            message.setJMSCorrelationID(String.valueOf(id));

            message.setStringProperty("VERSION", "900");
            message.setStringProperty("MSG_FORMAT", "ISO8583/1987");
            message.setStringProperty("TERMID_FORMAT", "2");
            message.setStringProperty("TERM_ID", String.valueOf(id));
            message.setStringProperty("TYPE_ID", type);
            message.setStringProperty("SOURCE_CONTEXT", ctx.channel().id() + " / " + id);
            message.setStringProperty("TARGET_CONTEXT", "9201 / 0");

            message.writeBytes(IsoUtil.compactBitmap(body));

            return message;
        });
    }

    private String resolveQueueName(String type) {
        // Verifica se destino eh autorizador OFFERING ou autorizador SWITCH
        return type.startsWith(OFFERING_MSG_TYPE) ? offeringQueue : switchQueue;
    }

    public static class ConnectionEvent {
        private String eventType;
        private String info;

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

}
