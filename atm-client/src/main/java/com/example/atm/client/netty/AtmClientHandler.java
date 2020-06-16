package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AtmClientHandler extends SimpleChannelInboundHandler<AtmMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AtmMessage msg) {
        System.err.println(msg.getBody());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
