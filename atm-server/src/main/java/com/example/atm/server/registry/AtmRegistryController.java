package com.example.atm.server.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/registry")
public class AtmRegistryController {

    @Autowired
    private AtmRegistry registry;

    @GetMapping("/")
    public Set<String> list() {
        return registry.getIds();
    }

}
