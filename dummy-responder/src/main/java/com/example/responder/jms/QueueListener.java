package com.example.responder.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.nio.charset.StandardCharsets;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${responder.upper-case}")
    private boolean upperCase;

    @JmsListener(destination = "DEV.QUEUE.1", concurrency = "2")
    public void receiveMessage(byte[] body, Message message) throws JMSException {
        //LOG.info("Received from queue: {}", body);
        //logJmsProperties(message);

        String sourceContext = message.getStringProperty("SOURCE_CONTEXT");
        String targetContext = message.getStringProperty("TARGET_CONTEXT");

        Destination replyTo = message.getJMSReplyTo();
        LOG.debug("replyTo = {} targetContext = {}", replyTo, targetContext);

        jmsTemplate.send(replyTo, session -> {
            BytesMessage response = session.createBytesMessage();
            response.setJMSCorrelationID(message.getJMSCorrelationID());

            response.setStringProperty("SOURCE_CONTEXT", targetContext);
            response.setStringProperty("TARGET_CONTEXT", sourceContext);

            response.writeBytes(createResponse(body));

            return response;
        });
    }

    private byte[] createResponse(byte[] data) {
        byte[] body = new byte[data.length - 12];
        System.arraycopy(data, 12, body, 0, body.length);

        if (upperCase) {
            body = new String(body, StandardCharsets.ISO_8859_1).toUpperCase().getBytes(StandardCharsets.ISO_8859_1);
        }

        byte[] response = new byte[data.length];
        System.arraycopy(data, 0, response, 0, 12);
        System.arraycopy(body, 0, response, 12, body.length);

        response[2]++;

        return response;
    }


    private void logJmsProperties(Message message) throws JMSException {
        var names = message.getPropertyNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            String value = message.getStringProperty(key);
            LOG.debug("{} = {}", key, value);
        }
    }

}
