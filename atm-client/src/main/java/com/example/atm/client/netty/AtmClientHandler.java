package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AtmClientHandler extends SimpleChannelInboundHandler<AtmMessage> {

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
        String body = msg.getBody();

        String mti = body.substring(0, 4);
        String bitmap = body.substring(4, 20);
        String text = body.substring(20);

        System.err.printf("[%s,%s] %s%n", mti, bitmap, text);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
