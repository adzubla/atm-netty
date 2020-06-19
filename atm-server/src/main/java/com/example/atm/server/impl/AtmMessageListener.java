package com.example.atm.server.impl;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.server.conn.ConnectionKey;
import com.example.atm.server.conn.ConnectionManager;
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

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        LOG.info(ctx.channel().remoteAddress() + " connected");
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        LOG.info(ctx.channel().remoteAddress() + " disconnected");
        connectionManager.remove(ctx);
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, AtmMessage msg) {
        LOG.debug("onMessage: {} {}", ctx, msg);

        if (isMessageValid(msg)) {
            dispatch(ctx, msg);
        }
    }

    private boolean isMessageValid(AtmMessage msg) {
        return true;
    }

    private void dispatch(ChannelHandlerContext ctx, AtmMessage msg) {
        ConnectionKey connectionKey = new ConnectionKey(msg.getId());

        connectionManager.add(connectionKey, ctx);

        String queueName = resolveQueueName(msg);

        jmsTemplate.send(queueName, session -> {
            String text = msg.getId() + msg.getBody();

            LOG.debug("Sending to {}: {}", queueName, text);
            TextMessage message = session.createTextMessage(text);
            message.setJMSReplyTo(replyToHolder.getReplyToQueue());
            message.setJMSCorrelationID(connectionKey.getId());

            return message;
        });
    }

    private String resolveQueueName(AtmMessage msg) {
        return "DEV.QUEUE.1";
    }

}
