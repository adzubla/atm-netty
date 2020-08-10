package com.example.atm.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


@Service
public class RoutingService {
    private static final Logger LOG = LoggerFactory.getLogger(RoutingService.class);

    @Value("#{atmServerProperties.routingLocation}")
    private String routingLocation;

    private List<RoutingRule> rules;

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Reading registry from {}", routingLocation);
        try (FileReader reader = new FileReader(routingLocation)) {
            load(reader);
        }
    }

    public List<String> getTokens(String str) {
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(str, "\t");
        while (tokenizer.hasMoreElements()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    public void load(Reader reader) throws IOException {
        List<RoutingRule> newRules = new ArrayList<>();

        LineNumberReader in = new LineNumberReader(reader);

        String line;
        while ((line = in.readLine()) != null) {
            LOG.debug("line = {}", line);
            if (!line.matches("^#.*")) {

                //ATM_ID\tMSG_ID\tDEST
                List<String> tokens = getTokens(line);

                if (tokens.size() == 3) {
                    newRules.add(new RoutingRule(in.getLineNumber(), tokens.get(0), tokens.get(1), tokens.get(2)));
                } else {
                    LOG.warn("Pattern problem '{}'", line);
                }
            }
        }

        this.rules = newRules;
        LOG.info("Loaded {} items", newRules.size());
    }

    public String getDestinationQueue(String id, String type) {
        //Analisa as regras de roteamento, baseado em CODIGO ATM e/ou CODIGO MENSAGEM
        for (RoutingRule rule : rules) {

            if (rule.getAtmId().equals("*") || rule.getAtmIdPattern().matcher(id).matches()) {
                if (rule.getMsgId().equals("*") || rule.getMsgId().equals(type)) {
                    LOG.debug("Match {}", rule);
                    return rule.getDestinationQueue();
                }
            }

        }

        //Nao satisfez nenhuma regra, possivelmente FALHA no arquivo de roteamento
        throw new IllegalStateException("No rules found for type " + type + " and id " + id);
    }

    public List<RoutingRule> getRules() {
        return rules;
    }

}
