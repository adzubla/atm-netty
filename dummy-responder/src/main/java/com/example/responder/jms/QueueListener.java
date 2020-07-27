package com.example.responder.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Service
public class QueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "DEV.QUEUE.1", concurrency = "2")
    public void receiveMessage(String body, Message message) throws JMSException {
        LOG.info("Received from queue: {}", body);

        String sourceContext = message.getStringProperty("SOURCE_CONTEXT");
        LOG.info("sourceContext = {}", sourceContext);

        String targetContext = message.getStringProperty("TARGET_CONTEXT");
        LOG.info("targetContext = {}", targetContext);

        Destination replyTo = message.getJMSReplyTo();

        jmsTemplate.send(replyTo, session -> {
            TextMessage textMessage = session.createTextMessage(createResponse(body));
            textMessage.setJMSCorrelationID(message.getJMSCorrelationID());

            textMessage.setStringProperty("SOURCE_CONTEXT", targetContext);
            textMessage.setStringProperty("TARGET_CONTEXT", sourceContext);

            return textMessage;
        });
    }

    private String createResponse(String data) {
        return data.toUpperCase();
    }

}
