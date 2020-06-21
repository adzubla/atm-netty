package com.example.atm.server.event;

import com.example.atm.server.conn.ConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

@Component
public class EventSender {
    private static final Logger LOG = LoggerFactory.getLogger(EventSender.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("#{atmServerConfig.eventQueueName}")
    private String eventQueue;

    @Scheduled(fixedRateString = "#{atmServerConfig.eventSendRate}")
    public void connectionStats() {
        Collection<ConnectionManager.ConnectionData> list = connectionManager.list();
        LOG.debug("{} connections", list.size());

        send(list);
    }

    public void send(Object obj) {
        sendText(serialize(obj));
    }

    private void sendText(String text) {
        jmsTemplate.send(eventQueue, session -> session.createTextMessage(text));
    }

    private String serialize(Object data) {
        try {
            Writer out = new StringWriter();
            objectMapper.writeValue(out, data);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
