package com.example.atm.server.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.example.atm.netty.codec.atm.AtmMessage.ID_LENGTH;

@Service
public class AtmRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(AtmRegistry.class);

    private static final Pattern ONLY_DIGITS_PATTERN = Pattern.compile("^[0-9]+$");

    @Value("#{atmServerProperties.registryDisable}")
    private boolean disable;

    @Value("#{atmServerProperties.registryLocation}")
    private String location;

    private Map<String, String> registry = Collections.emptyMap();

    @PostConstruct
    public void init() throws IOException {
        if (disable) {
            LOG.debug("Ignoring registry");
            return;
        }

        LOG.debug("Reading registry from {}", location);
        try (FileReader reader = new FileReader(location)) {
            load(reader);
        }
    }

    public boolean isRegistered(String id) {
        return disable || registry.containsKey(id);
    }

    public Set<String> getIds() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    public void load(Reader reader) throws IOException {
        Map<String, String> newRegistry = new HashMap<>();

        BufferedReader in = new BufferedReader(reader);

        int c = 0;
        String line;
        while ((line = in.readLine()) != null) {
            LOG.debug("line = {}", line);
            if (line.length() == ID_LENGTH && ONLY_DIGITS_PATTERN.matcher(line).matches()) {
                c++;
                newRegistry.put(line, line);
            } else {
                LOG.warn("Ignoring '{}'", line);
            }
        }

        this.registry = newRegistry;
        LOG.info("Loaded {} items", c);
    }

}
