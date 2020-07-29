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

import static com.example.atm.server.impl.IsoUtil.dump;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @JmsListener(destination = "REPLY_TO_DYNAMIC_QUEUE", concurrency = "#{atmServerProperties.mqListenerConcurrency}", containerFactory = "queueConnectionFactory")
    public void receive(String body, Message message) throws JMSException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received from queue: {}", dump(body));
            logJmsProperties(message);
        }

        String targetContext = message.getStringProperty("TARGET_CONTEXT");
        LOG.debug("targetContext = {}", targetContext);

        String id = getId(targetContext);

        ConnectionManager.ConnectionData connectionData = connectionManager.get(id);

        if (connectionData == null) {
            LOG.warn("Corresponding connection not found. Discarding: {}", dump(body));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Responding to client: {}", dump(body));
            }

            AtmMessage msg = new AtmMessage(id, body);

            connectionData.countOutput();
            connectionData.channelHandlerContext().writeAndFlush(msg);
        }
    }

    private void logJmsProperties(Message message) throws JMSException {
        var names = message.getPropertyNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            String value = message.getStringProperty(key);
            LOG.debug("{} = {}", key, value);
        }
    }

    private String getId(String targetContext) {
        int i = targetContext.indexOf('/');
        return targetContext.substring(i + 2);
    }

}
