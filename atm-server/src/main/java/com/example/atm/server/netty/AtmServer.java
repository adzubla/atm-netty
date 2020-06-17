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
import com.example.atm.server.impl.AtmServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public final class AtmServer {

    private final AtmServerConfig config;
    private final AtmServerListener listener;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private EventExecutorGroup cryptoGroup;
    private EventExecutorGroup handlerGroup;

    public AtmServer(AtmServerConfig config, AtmServerListener listener) {
        this.config = config;
        this.listener = listener;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(config.getBossThreads());
        workerGroup = new NioEventLoopGroup(config.getWorkerThreads());

        cryptoGroup = new DefaultEventExecutorGroup(config.getCryptoThreads());
        handlerGroup = new DefaultEventExecutorGroup(config.getHandlerThreads());

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFrameDecoder());
                        pipeline.addLast(cryptoGroup, new CryptoDecoder());
                        pipeline.addLast(new MacDecoder());
                        pipeline.addLast(new HeaderDecoder());
                        pipeline.addLast(new AtmDecoder());

                        pipeline.addLast(new LengthPrepender());
                        pipeline.addLast(cryptoGroup, new CryptoEncoder());
                        pipeline.addLast(new MacEncoder());
                        pipeline.addLast(new HeaderEncoder());
                        pipeline.addLast(new AtmEncoder());

                        pipeline.addLast(handlerGroup, new AtmServerHandler(listener));
                    }
                });

        b.bind(config.getSocketPort()).sync().await();
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
