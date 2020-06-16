package com.example.atm.server;

import com.example.atm.server.netty.AtmServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AtmServerService {
    private static final Logger LOG = LoggerFactory.getLogger(AtmServerService.class);

    private AtmServer server;
    private int port = 8992;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @PostConstruct
    public void init() throws InterruptedException {
        LOG.info("Starting...");
        server = new AtmServer(port);
        server.start();
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutdown...");
        server.shutdown();
    }

}
