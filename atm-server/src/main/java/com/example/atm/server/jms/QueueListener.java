package com.example.atm.server.jms;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.server.conn.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @JmsListener(destination = "REPLY_TO_DYNAMIC_QUEUE", concurrency = "#{atmServerProperties.mqListenerConcurrency}", containerFactory = "queueConnectionFactory")
    public void receive(String body, Message message) throws JMSException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received from queue: <{}>", body.substring(0, 16) + "...");
        }

        String targetContext = message.getStringProperty("TARGET_CONTEXT");
        LOG.debug("targetContext = {}", targetContext);

        String id = getId(targetContext);

        ConnectionManager.ConnectionData connectionData = connectionManager.get(id);

        if (connectionData == null) {
            LOG.warn("Corresponding connection not found. Discarding: <{}>", body.substring(0, 16) + "...");
        } else {
            LOG.debug("Responding to client: <{}>", body.substring(0, 16) + "...");

            AtmMessage msg = new AtmMessage(id, body);

            connectionData.countOutput();
            connectionData.channelHandlerContext().writeAndFlush(msg);
        }
    }

    private String getId(String targetContext) {
        int i = targetContext.indexOf('/');
        return targetContext.substring(i + 2);
    }

}
