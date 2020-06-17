package com.example.atm.server.impl;

import com.example.atm.server.netty.AtmServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AtmServerService {
    private static final Logger LOG = LoggerFactory.getLogger(AtmServerService.class);

    @Value("${atm.socket.port}")
    private int port;

    @Autowired
    private AtmMessageListener listener;

    private AtmServer server;

    @PostConstruct
    public void init() throws InterruptedException {
        LOG.info("Starting");
        server = new AtmServer(port, listener);
        server.start();
        LOG.info("Started");
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutdown");
        server.shutdown();
    }

}
