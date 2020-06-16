package com.example.atm.server.impl;

import com.example.atm.netty.codec.header.HeaderData;
import com.example.atm.server.conn.ConnectionId;
import com.example.atm.server.conn.ConnectionManager;
import com.example.atm.server.jms.ReplyToHolder;
import com.example.atm.server.netty.AtmServerListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.concurrent.ExecutorService;

@Component
public class AtmMessageListener implements AtmServerListener {
    private static final Logger LOG = LoggerFactory.getLogger(AtmMessageListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    ReplyToHolder replyToHolder;

    @Autowired
    ExecutorService executorService;

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        LOG.info(ctx.channel().remoteAddress() + " connected");
        ctx.writeAndFlush("Welcome to the machine\n");
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        LOG.info(ctx.channel().remoteAddress() + " disconnected");
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, String message, HeaderData headerData) {
        LOG.debug("onMessage: {} {} {}", ctx, headerData, message);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean ok = isMessageValid(message);
                if (ok) {
                    dispatch(ctx, message, headerData);
                }
            }
        });
    }

    private boolean isMessageValid(String message) {
        return true;
    }

    private void dispatch(ChannelHandlerContext channelHandlerContext, String message, HeaderData headerData) {
        ConnectionId id = new ConnectionId(headerData.getIdTerminal());

        connectionManager.add(id, channelHandlerContext);

        jmsTemplate.send("DEV.QUEUE.1", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                LOG.debug("**** createMessage {}", session);
                String s = headerData.getIdTerminal() + message;

                LOG.debug("Sending to queue: {}", s);
                TextMessage message = session.createTextMessage(s);
                message.setJMSReplyTo(replyToHolder.getReplyToQueue());
                message.setJMSCorrelationID(id.getId());

                return message;
            }
        });
    }

}
