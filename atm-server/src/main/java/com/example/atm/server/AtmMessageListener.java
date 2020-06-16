package com.example.atm.server;

import com.example.atm.netty.codec.header.HeaderData;
import com.example.atm.server.netty.AtmServerListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

@Component
public class AtmMessageListener implements AtmServerListener {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("Welcome to secure chat service!");
        channels.add(ctx.channel());
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + " disconnected");
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, String message, HeaderData headerData) {
        // Send the received message to all channels but the current one.
        System.out.println("channels: " + channels.size());
        for (Channel c : channels) {
            if (c != ctx.channel()) {
                c.writeAndFlush("[" + headerData.getIdTerminal() + "|" + ctx.channel().remoteAddress() + "] " + message);
            } else {
                c.writeAndFlush("[" + headerData.getIdTerminal() + "|me] " + message);
            }
        }

        // Close the connection if the client has sent 'bye'.
        if ("bye".equals(message.toLowerCase())) {
            ctx.close();
        }
    }

}
