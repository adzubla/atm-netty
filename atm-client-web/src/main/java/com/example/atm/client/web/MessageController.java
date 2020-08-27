package com.example.atm.client.web;

import com.example.atm.client.netty.AtmClient;
import com.example.atm.netty.codec.atm.AtmMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.math.BigInteger;

@Controller
public class MessageController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Value("${atm-server.host}")
    private String host;

    @Value("${atm-server.port}")
    private int port;

    private AtmClient client;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) throws InterruptedException {
        LOG.info("*** Connect");
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        LOG.debug("headers = " + headers);

        client = new AtmClient(host, port, new WebAtmClientHandler(template));
        client.connect();
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        LOG.info("*** Disconnect");
        client.close();
    }

    @MessageMapping("/receive/{atmId}")
    public void receive(ReceiveMessage message, @DestinationVariable Long atmId) throws Exception {
        LOG.debug("*** Received from " + atmId + ": " + message.getName());

        AtmMessage atmMessage = new AtmMessage(atmId, createIsoMessage(message.getName()));
        client.write(atmMessage, true);
    }

    private String createIsoMessage(String line) {
        String mti = "0100";
        StringBuilder bitmapBin = new StringBuilder("0010001000010000000000000001000100000010110000000100100000000101");
        String bitmapHex = (new BigInteger(bitmapBin.toString(), 2)).toString(16).toUpperCase();
        assert bitmapHex.length() == 16;
        return mti + bitmapHex + line;
    }

}