package com.example.atm.server.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AtmServerHandler extends SimpleChannelInboundHandler<AtmMessage> {

    private final AtmServerListener listener;

    public AtmServerHandler(AtmServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        listener.onConnect(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AtmMessage msg) {
        listener.onMessage(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        listener.onDisconnect(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
