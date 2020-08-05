package com.example.atm.server.jms;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.util.IsoUtil;
import com.example.atm.server.conn.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;

import static com.example.atm.netty.codec.util.IsoUtil.dump;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @JmsListener(destination = "REPLY_TO_DYNAMIC_QUEUE", concurrency = "#{atmServerProperties.mqListenerConcurrency}", containerFactory = "queueConnectionFactory")
    public void receive(byte[] data, Message message) throws JMSException {
        String body = IsoUtil.expandBitmap(data);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Received from queue: {}", dump(body));
        }

        String targetContext = message.getStringProperty("TARGET_CONTEXT");
        LOG.debug("targetContext = {}", targetContext);

        Long id = getId(targetContext);

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

    private Long getId(String targetContext) {
        int i = targetContext.indexOf('/');
        return Long.parseLong(targetContext.substring(i + 2));
    }

}
