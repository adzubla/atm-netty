package com.example.atm.client.web;

import com.example.atm.client.netty.AtmClientHandler;
import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.header.HeaderData;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

public class WebAtmClientHandler extends AtmClientHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WebAtmClientHandler.class);

    private final SimpMessagingTemplate template;

    public WebAtmClientHandler(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOG.debug("Connected: " + ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOG.debug("Disconnected: " + ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AtmMessage msg) {
        Byte type = ctx.channel().attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).get();
        String response;
        if (type == HeaderData.PONG) {
            response = "[PONG]";
        } else {
            String body = new String(msg.getBody(), StandardCharsets.ISO_8859_1);

            String mti = body.substring(0, 4);
            String bitmap = body.substring(4, 20);
            String text = body.substring(20);

            response = String.format("[%s,%s] %s%n", mti, bitmap, text);
        }

        LOG.debug("Response: {}", response);
        template.convertAndSend("/topic/response/" + msg.getId(), new ResponseMessage(HtmlUtils.htmlEscape(response)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
