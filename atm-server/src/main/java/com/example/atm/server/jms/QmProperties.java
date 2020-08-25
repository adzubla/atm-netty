package com.example.atm.server.jms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties("qm")
@Configuration
public class QmProperties {

    private List<ExtendedMQConfigurationProperties> list;

    public List<ExtendedMQConfigurationProperties> getList() {
        return list;
    }

    public void setList(List<ExtendedMQConfigurationProperties> list) {
        this.list = list;
    }

}
