package com.example.atm.server.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AtmServerConfig {

    @Value("${atm.server.threads}")
    public int quantThreads;

    @Bean
    public ExecutorService threadPoolExecutor() {
        return Executors.newFixedThreadPool(quantThreads);
    }

}
