package com.example.atm.server.conn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/connection")
public class ConnectionManagerController {

    @Autowired
    private ConnectionManager connectionManager;

    @GetMapping("/")
    public Collection<ConnectionManager.ConnectionData> list() {
        return connectionManager.list();
    }

    @GetMapping("/{id}")
    public ConnectionManager.ConnectionData get(@PathVariable String id) {
        return connectionManager.get(id);
    }

    @DeleteMapping("/{id}")
    public void close(@PathVariable String id) {
        if ("all".equals(id)) {
            connectionManager.removeAll();
        } else {
            connectionManager.remove(id);
        }
    }

}
