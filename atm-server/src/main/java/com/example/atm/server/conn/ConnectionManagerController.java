package com.example.atm.server.conn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class ConnectionManagerController {

    @Autowired
    private ConnectionManager connectionManager;

    @GetMapping("/connections")
    public Collection<ConnectionManager.ConnectionData> list() {
        return connectionManager.list();
    }

}
