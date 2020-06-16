package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class AtmClient {

    private final String host;
    private final int port;

    private EventLoopGroup group;
    private Channel channel;

    public AtmClient(String id, String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new AtmClientInitializer());

        channel = b.connect(host, port).sync().channel();
    }

    public void write(AtmMessage msg) throws InterruptedException {
        ChannelFuture lastWriteFuture = channel.writeAndFlush(msg);
        if (lastWriteFuture != null) {
            lastWriteFuture.sync();
        }
    }

    public void close() throws InterruptedException {
        channel.closeFuture().sync();
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

}
