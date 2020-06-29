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
import com.example.atm.server.AtmServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AtmServer {
    private static final Logger LOG = LoggerFactory.getLogger(AtmServer.class);

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final EventExecutorGroup cryptoGroup;
    private final EventExecutorGroup handlerGroup;

    public AtmServer(AtmServerConfig config, AtmServerListener listener) {
        bossGroup = TransportType.newEventLoopGroup(config.getBossThreads());
        workerGroup = TransportType.newEventLoopGroup(config.getWorkerThreads());

        cryptoGroup = new DefaultEventExecutorGroup(config.getCryptoThreads());
        handlerGroup = new DefaultEventExecutorGroup(config.getHandlerThreads());

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(TransportType.getServerChannelClass())
                .option(ChannelOption.SO_BACKLOG, config.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isSoKeepalive())
                .childOption(ChannelOption.TCP_NODELAY, config.isTcpNodelay())
                .handler(new LoggingHandler(LogLevel.INFO))
                .localAddress(config.getSocketPort())
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
        bootstrap.validate();
    }

    public void start() throws InterruptedException {
        LOG.info("Starting");
        bootstrap.bind().sync().await();
    }

    public void shutdown() throws InterruptedException {
        LOG.info("Shutdown workerGroup");
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync().await();
        }
        LOG.info("Shutdown bossGroup");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync().await();
        }
        LOG.info("Shutdown cryptoGroup");
        if (cryptoGroup != null) {
            cryptoGroup.shutdownGracefully().sync().await();
        }
        LOG.info("Shutdown handlerGroup");
        if (handlerGroup != null) {
            handlerGroup.shutdownGracefully().sync().await();
        }
    }

}
