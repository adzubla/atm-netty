package com.example.atm.server.impl;

import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.util.IsoUtil;
import com.example.atm.server.conn.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import static com.example.atm.netty.codec.util.IsoUtil.dump;

@Service
public class ResponseListener implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    public void onMessage(Message message) {
        if (message instanceof BytesMessage) {
            try {
                byte[] data = message.getBody(byte[].class);
                String targetContext = message.getStringProperty("TARGET_CONTEXT");

                handleMessage(data, targetContext);
            } catch (JMSException ex) {
                LOG.error("JMS error. Discarding message {}", message, ex);
            }
        } else {
            LOG.error("Message must be of type BytesMessage. Discarding message {}", message);
        }
    }

    private void handleMessage(byte[] data, String targetContext) {
        String body = IsoUtil.expandBitmap(data);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Received from queue: {}", dump(body));
        }

        Long id = getId(targetContext);

        ConnectionManager.ConnectionData connectionData = connectionManager.get(id);

        if (connectionData == null) {
            LOG.warn("Corresponding connection not found. Discarding: {}", dump(body));
        } else {
            AtmMessage msg = new AtmMessage(id, body);

            ChannelHandlerContext channelHandlerContext = connectionData.channelHandlerContext();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Responding to ATM {}: {} {}", id, dump(body), channelHandlerContext.channel());
            }
            connectionData.countOutput();
            channelHandlerContext.writeAndFlush(msg);
        }
    }

    private Long getId(String targetContext) {
        int i = targetContext.indexOf('/');
        return Long.parseLong(targetContext.substring(i + 2));
    }

}
