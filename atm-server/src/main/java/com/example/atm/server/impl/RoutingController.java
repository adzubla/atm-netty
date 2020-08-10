package com.example.atm.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routingrules")
public class RoutingController {

    @Autowired
    private RoutingService routingRules;

    @GetMapping("/")
    public List<RoutingRule> list() {
        return routingRules.getRules();
    }

}
