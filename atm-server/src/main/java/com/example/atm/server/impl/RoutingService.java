package com.example.atm.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

        BufferedReader in = new BufferedReader(reader);

        int rule = 0;
        String line;
        while ((line = in.readLine()) != null) {
            LOG.debug("line = {}", line);
            if (!line.matches("^#.*")) {
                StringTokenizer tok = new StringTokenizer(line, "\t");

                //ATM_ID\tMSG_ID\tDEST
                LOG.debug("tokens = {}", tok.countTokens());
                if (tok.countTokens() == 3) {
                    List<String> routingTableLine = getTokens(line);
                    rule++;
                    newRules.add(new RoutingRule(routingTableLine.get(0), routingTableLine.get(1), routingTableLine.get(2)));
                } else {
                    LOG.warn("Pattern problem '{}'", line);
                }
            } else {
                LOG.warn("Ignoring '{}'", line);
            }
        }

        this.rules = newRules;
        LOG.info("Loaded {} items", rule);
    }

    //Analisa as regras de roteamento, baseado em CODIGO ATM e/ou CODIGO MENSAGEM
    public String getDestination(String id, String type) {

        int i = 0;
        for (RoutingRule rule : rules) {
            LOG.debug("rule = {}", rule);

            //Para ATM_ID utilizar expressao regular
            if (rule.getAtmId().equals("*") || id.matches(rule.getAtmId())) {
                if (rule.getMsgId().equals("*") || rule.getMsgId().equals(type)) {

                    LOG.debug("Found rule {} (Queue={})!", i, rule.getDestinationQueue());
                    return rule.getDestinationQueue();
                }
            }
            i++;
        }

        //Nao satisfez nenhuma regra, possivelmente FALHA no arquivo de roteamento
        throw new IllegalStateException("No rules found for type " + type + " and id " + id);
    }

    public List<RoutingRule> getRules() {
        return rules;
    }

}
