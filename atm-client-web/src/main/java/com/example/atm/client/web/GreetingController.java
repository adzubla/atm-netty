package com.example.atm.client.web;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("*** Connect");
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        System.out.println("*** Disconnect");
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        System.out.println("*** Received " + message.getName());
        String response = message.getName().toUpperCase();
        return new Greeting(HtmlUtils.htmlEscape(response));
    }

}