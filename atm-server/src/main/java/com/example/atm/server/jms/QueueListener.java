package com.example.atm.server.jms;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.server.conn.ConnectionKey;
import com.example.atm.server.conn.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import static com.example.atm.netty.codec.atm.AtmMessage.ID_LENGTH;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @JmsListener(destination = "REPLY_TO_DYNAMIC_QUEUE", concurrency = "2")
    public void receive(String message) {
        LOG.debug("Received from queue: {}", message);

        ConnectionKey cid = new ConnectionKey(message.substring(0, ID_LENGTH));
        String body = message.substring(ID_LENGTH);

        ConnectionManager.ConnectionData connectionData = connectionManager.get(cid);

        if (connectionData == null) {
            LOG.debug("Discarding: {}", body);
        } else {
            LOG.debug("Responding to client: {}", body);

            AtmMessage msg = new AtmMessage(cid.getId(), body);

            connectionData.getChannelHandlerContext().writeAndFlush(msg);
        }
    }

}
