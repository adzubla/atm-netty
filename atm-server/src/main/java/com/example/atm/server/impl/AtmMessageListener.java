package com.example.atm.server.impl;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.server.conn.ConnectionManager;
import com.example.atm.server.event.EventSender;
import com.example.atm.server.jms.ReplyToHolder;
import com.example.atm.server.netty.AtmServerListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

@Component
public class AtmMessageListener implements AtmServerListener {
    private static final Logger LOG = LoggerFactory.getLogger(AtmMessageListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ReplyToHolder replyToHolder;

    @Autowired
    private AtmRegistry registry;

    @Autowired
    EventSender eventSender;

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        LOG.info("{} connected", ctx);
        connectionManager.add(ctx);

        ConnectionEvent event = new ConnectionEvent();
        event.setType("CONNECT");
        event.setInfo(ctx.toString());
        eventSender.send(event);
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        LOG.info("{} disconnected", ctx);
        connectionManager.remove(ctx);

        ConnectionEvent event = new ConnectionEvent();
        event.setType("DISCONNECT");
        event.setInfo(ctx.toString());
        eventSender.send(event);
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, AtmMessage msg) {
        LOG.debug("{} received {}", ctx, msg);

        if (isMessageValid(msg)) {
            dispatch(ctx, msg);
        } else {
            LOG.warn("Discarding message {}", msg);
        }
    }

    private boolean isMessageValid(AtmMessage msg) {
        return registry.isRegistered(msg.getId());
    }

    private void dispatch(ChannelHandlerContext ctx, AtmMessage msg) {
        String id = msg.getId();

        connectionManager.add(id, ctx);

        String queueName = resolveQueueName(msg);

        jmsTemplate.send(queueName, session -> {
            // coloca o id na mensagem a ser transmitida
            String text = id + msg.getBody();

            LOG.debug("Sending to {}: {}", queueName, text);
            TextMessage message = session.createTextMessage(text);
            message.setJMSReplyTo(replyToHolder.getReplyToQueue());
            message.setJMSCorrelationID(id);

            return message;
        });
    }

    private String resolveQueueName(AtmMessage msg) {
        return "DEV.QUEUE.1";
    }

    public static class ConnectionEvent {
        private String type;
        private String info;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

}
