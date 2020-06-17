package com.example.atm.server.netty;

import com.example.atm.netty.codec.atm.AtmDecoder;
import com.example.atm.netty.codec.atm.AtmEncoder;
import com.example.atm.netty.codec.crypto.CryptoDecoder;
import com.example.atm.netty.codec.crypto.CryptoEncoder;
import com.example.atm.netty.codec.header.HeaderDecoder;
import com.example.atm.netty.codec.header.HeaderEncoder;
import com.example.atm.netty.codec.length.LengthFrameDecoder;
import com.example.atm.netty.codec.length.LengthPrepender;
import com.example.atm.netty.codec.mac.MacDecoder;
import com.example.atm.netty.codec.mac.MacEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class AtmServer {

    private final int port;
    private final AtmServerListener listener;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public AtmServer(int port, AtmServerListener listener) {
        this.port = port;
        this.listener = listener;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFrameDecoder());
                        pipeline.addLast(new CryptoDecoder());
                        pipeline.addLast(new MacDecoder());
                        pipeline.addLast(new HeaderDecoder());
                        pipeline.addLast(new AtmDecoder());

                        pipeline.addLast(new LengthPrepender());
                        pipeline.addLast(new CryptoEncoder());
                        pipeline.addLast(new MacEncoder());
                        pipeline.addLast(new HeaderEncoder());
                        pipeline.addLast(new AtmEncoder());

                        pipeline.addLast(new AtmServerHandler(listener));
                    }
                });

        b.bind(port).sync().await();
    }

    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

}
