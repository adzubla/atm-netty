package com.example.atm.server.jms;

import com.example.atm.server.impl.AtmRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigTopicListener {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigTopicListener.class);

    @Autowired
    private AtmRegistry registry;

    @JmsListener(destination = "#{atmServerConfig.configTopicName}", containerFactory = "topicConnectionFactory")
    public void receive(String message) throws IOException {
        Map<String, String> map = new HashMap<>();

        BufferedReader reader = new BufferedReader(new StringReader(message));
        String line;
        while ((line = reader.readLine()) != null) {
            map.put(line, line);
        }

        LOG.info("Updating configuration with {} items", map.size());
        registry.setRegistry(map);
    }

}
