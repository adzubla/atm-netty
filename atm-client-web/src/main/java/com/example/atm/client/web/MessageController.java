package com.example.atm.client.web;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MessageController {

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("*** Connect");
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        System.out.println("headers = " + headers);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        System.out.println("*** Disconnect");
    }

    @MessageMapping("/receive/{atmId}")
    @SendTo("/topic/response/{atmId}")
    public ResponseMessage receive(ReceiveMessage message, @DestinationVariable Long atmId) throws Exception {
        System.out.println("*** Received from " + atmId + ": " + message.getName());
        String response = message.getName().toUpperCase();
        return new ResponseMessage(HtmlUtils.htmlEscape(response));
    }

}