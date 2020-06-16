package com.example.atm.server.impl;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.server.conn.ConnectionId;
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
import java.util.concurrent.ExecutorService;

@Component
public class AtmMessageListener implements AtmServerListener {
    private static final Logger LOG = LoggerFactory.getLogger(AtmMessageListener.class);

    private ConnectionManager connectionManager;
    private JmsTemplate jmsTemplate;
    private ReplyToHolder replyToHolder;
    private ExecutorService executorService;

    @Autowired
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Autowired
    public void setReplyToHolder(ReplyToHolder replyToHolder) {
        this.replyToHolder = replyToHolder;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        LOG.info(ctx.channel().remoteAddress() + " connected");
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        LOG.info(ctx.channel().remoteAddress() + " disconnected");
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, AtmMessage msg) {
        LOG.debug("onMessage: {} {}", ctx, msg);

        executorService.execute(() -> {
            if (isMessageValid(msg)) {
                dispatch(ctx, msg);
            }
        });
    }

    private boolean isMessageValid(AtmMessage msg) {
        return true;
    }

    private void dispatch(ChannelHandlerContext ctx, AtmMessage msg) {
        ConnectionId cid = new ConnectionId(msg.getId());

        connectionManager.add(cid, ctx);

        String queueName = "DEV.QUEUE.1";

        jmsTemplate.send(queueName, session -> {
            String text = msg.getId() + msg.getBody();

            LOG.debug("Sending to {}: {}", queueName, text);
            TextMessage message = session.createTextMessage(text);
            message.setJMSReplyTo(replyToHolder.getReplyToQueue());
            message.setJMSCorrelationID(cid.getId());

            return message;
        });
    }

}
