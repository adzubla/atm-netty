package com.example.atm.server.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;

@Service
public class AtmRegistryTopicListener {
    private static final Logger LOG = LoggerFactory.getLogger(AtmRegistryTopicListener.class);

    @Autowired
    private AtmRegistry registry;

    @JmsListener(destination = "#{atmServerProperties.configTopicName}", containerFactory = "topicConnectionFactory")
    public void receive(String message) throws IOException {
        LOG.info("Updating configuration from topic");
        registry.load(new StringReader(message));
    }

}
