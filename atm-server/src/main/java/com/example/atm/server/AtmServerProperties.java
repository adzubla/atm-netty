package com.example.atm.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "atm.server")
public class AtmServerProperties {

    private int mqListenerConcurrency;

    private int socketPort;
    private int bossThreads;
    private int workerThreads;
    private int handlerThreads;
    private int cryptoThreads;

    private boolean tcpNodelay = true;
    private boolean soKeepalive = true;
    private int soBacklog = 1024;

    private String eventQueueName;
    private long eventSendRate = 60000;

    private String configTopicName;

    private String registryLocation;
    private boolean registryDisable = false;

    private String switchQueueName = "DEV.QUEUE.1";
    private String offeringQueueName = "DEV.QUEUE.3";

    public int getMqListenerConcurrency() {
        return mqListenerConcurrency;
    }

    public void setMqListenerConcurrency(int mqListenerConcurrency) {
        this.mqListenerConcurrency = mqListenerConcurrency;
    }

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

    public String getEventQueueName() {
        return eventQueueName;
    }

    public void setEventQueueName(String eventQueueName) {
        this.eventQueueName = eventQueueName;
    }

    public long getEventSendRate() {
        return eventSendRate;
    }

    public void setEventSendRate(long eventSendRate) {
        this.eventSendRate = eventSendRate;
    }

    public String getConfigTopicName() {
        return configTopicName;
    }

    public void setConfigTopicName(String configTopicName) {
        this.configTopicName = configTopicName;
    }

    public String getRegistryLocation() {
        return registryLocation;
    }

    public void setRegistryLocation(String registryLocation) {
        this.registryLocation = registryLocation;
    }

    public boolean isRegistryDisable() {
        return registryDisable;
    }

    public void setRegistryDisable(boolean registryDisable) {
        this.registryDisable = registryDisable;
    }

    public String getSwitchQueueName() {
        return switchQueueName;
    }

    public void setSwitchQueueName(String switchQueueName) {
        this.switchQueueName = switchQueueName;
    }

    public String getOfferingQueueName() {
        return offeringQueueName;
    }

    public void setOfferingQueueName(String offeringQueueName) {
        this.offeringQueueName = offeringQueueName;
    }

}
