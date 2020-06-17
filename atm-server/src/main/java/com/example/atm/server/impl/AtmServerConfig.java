package com.example.atm.server.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "atm.server")
public class AtmServerConfig {

    private int socketPort;
    private int bossThreads;
    private int workerThreads;
    private int handlerThreads;
    private int cryptoThreads;

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public int getHandlerThreads() {
        return handlerThreads;
    }

    public void setHandlerThreads(int handlerThreads) {
        this.handlerThreads = handlerThreads;
    }

    public int getCryptoThreads() {
        return cryptoThreads;
    }

    public void setCryptoThreads(int cryptoThreads) {
        this.cryptoThreads = cryptoThreads;
    }

}
