package com.example.atm.server.jms;

import com.example.atm.server.conn.ConnectionId;
import com.example.atm.server.conn.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @JmsListener(destination = "REPLY_TO_DYNAMIC_QUEUE", concurrency = "2")
    public void receive(String message) {
        LOG.debug("Received from queue: {}", message);

        ConnectionId id = new ConnectionId(message.substring(0, 4));
        String content = message.substring(4);

        ConnectionManager.ConnectionData connectionData = connectionManager.get(id);

        if (connectionData == null) {
            LOG.debug("Discarding: {}", content);
        } else {
            LOG.debug("Responding to client: {}", content);
            connectionData.getChannelHandlerContext().writeAndFlush(content);
        }
    }

}
