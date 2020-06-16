package com.example.atm.server.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.channel.ChannelHandlerContext;

public interface AtmServerListener {

    void onConnect(ChannelHandlerContext ctx);

    void onDisconnect(ChannelHandlerContext ctx);

    void onMessage(ChannelHandlerContext ctx, AtmMessage msg);
}
