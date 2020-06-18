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

    private boolean tcpNodelay = true;
    private boolean soKeepalive = true;
    private int soBacklog = 1024;

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

    public boolean isTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }

    public boolean isSoKeepalive() {
        return soKeepalive;
    }

    public void setSoKeepalive(boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

}
