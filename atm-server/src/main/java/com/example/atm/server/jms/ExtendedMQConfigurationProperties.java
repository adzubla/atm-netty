package com.example.atm.server.jms;

import com.ibm.mq.spring.boot.MQConfigurationProperties;

public class ExtendedMQConfigurationProperties extends MQConfigurationProperties {

    private String concurrency = "1";

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }
}
