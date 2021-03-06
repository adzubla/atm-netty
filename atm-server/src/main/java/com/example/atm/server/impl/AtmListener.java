package com.example.atm.server.impl;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.util.IsoUtil;
import com.example.atm.server.conn.ConnectionManager;
import com.example.atm.server.event.EventObject;
import com.example.atm.server.event.EventSender;
import com.example.atm.server.jms.JmsConfig;
import com.example.atm.server.jms.ReplyToHolder;
import com.example.atm.server.netty.AtmServerListener;
import com.example.atm.server.registry.AtmRegistry;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import java.util.Arrays;
import java.util.Collections;

import static com.example.atm.netty.codec.util.IsoUtil.dump;

@Component
public class AtmListener implements AtmServerListener {
    private static final Logger LOG = LoggerFactory.getLogger(AtmListener.class);

    private static final int MIN_MESSAGE_LENGTH = 20;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private JmsConfig jmsConfig;

    @Autowired
    private ReplyToHolder replyToHolder;

    @Autowired
    private AtmRegistry registry;

    @Autowired
    private RoutingService routingService;

    @Autowired
    private EventSender eventSender;

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        LOG.info("{} connected", ctx);
        connectionManager.add(ctx);

        ConnectionEvent event = new ConnectionEvent();
        event.setEventType("CONNECT");
        event.setInfo(ctx.toString());
        eventSender.send(Collections.singletonList(event));
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        LOG.info("{} disconnected", ctx);
        connectionManager.remove(ctx);

        ConnectionEvent event = new ConnectionEvent();
        event.setEventType("DISCONNECT");
        event.setInfo(ctx.toString());
        eventSender.send(Collections.singletonList(event));
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, AtmMessage msg) {

        //Verifica se tem no minimo o codigo da mensagem + mapa de bits
        if (msg.getBody().length < MIN_MESSAGE_LENGTH) {
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

        connectionManager.add(msg.getId(), ctx);

        String id = msg.getId().toString();
        byte[] body = msg.getBody();
        String type = new String(Arrays.copyOf(body, 4));

        Route route = routingService.getRoute(id, type);

        JmsTemplate jmsTemplate = jmsConfig.getJmsTemplate(route.getQueueManager());
        jmsTemplate.send(route.getDestinationQueue(), session -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending to {} {}: {}", route.getQueueManager(), route.getDestinationQueue(), dump(body));
            }

            BytesMessage message = session.createBytesMessage();
            message.setJMSReplyTo(replyToHolder.getReplyToQueue(route.getQueueManager()));
            message.setJMSCorrelationID(id);

            message.setStringProperty("VERSION", "300");
            message.setStringProperty("MSG_FORMAT", "ISO8583/1987");
            message.setStringProperty("TERMID_FORMAT", "2");
            message.setStringProperty("TERM_ID", id);
            message.setStringProperty("TYPE_ID", type);
            message.setStringProperty("SOURCE_CONTEXT", ctx.channel().id() + " / " + id);
            message.setStringProperty("TARGET_CONTEXT", "9201 / 0");

            message.writeBytes(IsoUtil.compactBitmap(body));

            return message;
        });
    }

    public static class ConnectionEvent implements EventObject {
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
