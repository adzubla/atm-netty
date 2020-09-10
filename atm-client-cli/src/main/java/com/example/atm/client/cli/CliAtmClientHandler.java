package com.example.atm.client.cli;

import com.example.atm.client.netty.AtmClientHandler;
import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.header.HeaderData;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class CliAtmClientHandler extends AtmClientHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.err.println("Connected: " + ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.err.println("Disconnected: " + ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AtmMessage msg) {
        Byte type = ctx.channel().attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).get();
        if (type == HeaderData.PONG) {
            System.err.println("[PONG]");
        } else {
            String body = new String(msg.getBody(), StandardCharsets.ISO_8859_1);

            String mti = body.substring(0, 4);
            String bitmap = body.substring(4, 20);
            String text = body.substring(20);

            System.err.printf("[%s,%s] %s%n", mti, bitmap, text);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
