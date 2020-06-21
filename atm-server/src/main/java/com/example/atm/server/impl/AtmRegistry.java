package com.example.atm.server.impl;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class AtmRegistry {

    private Map<String, String> registry = Collections.<String, String>emptyMap();

    public void setRegistry(Map<String, String> registry) {
        this.registry = registry;
    }

    public boolean isRegistered(String id) {
        return registry.containsKey(id);
    }

}
