package com.example.atm.server.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoutingServiceTest {

    static RoutingService routingService = new RoutingService();

    @BeforeAll
    public static void init() throws IOException {
        InputStream in = RoutingServiceTest.class.getResourceAsStream("/routingTable.txt");
        InputStreamReader reader = new InputStreamReader(in);

        routingService.load(reader);
    }

    @Test
    void getDestination() {
        assertEquals("DEV.QUEUE.3", routingService.getDestination("9901", "9380")); // match rule 0
        assertEquals("DEV.QUEUE.2", routingService.getDestination("9901", "7200")); // match rule 1
        assertEquals("DEV.QUEUE.9", routingService.getDestination("9901", "9999")); // match rule 2

        assertEquals("DEV.QUEUE.9", routingService.getDestination("992", "1000")); // match rule 3
        assertEquals("DEV.QUEUE.1", routingService.getDestination("991", "9999")); // match rule 4

        assertEquals("DEV.QUEUE.3", routingService.getDestination("99", "9380")); // match rule 5
        assertEquals("DEV.QUEUE.1", routingService.getDestination("99", "9999")); // match rule 6
    }

}
