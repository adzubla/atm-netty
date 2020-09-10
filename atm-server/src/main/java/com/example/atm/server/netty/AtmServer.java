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
import com.example.atm.server.AtmServerProperties;
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

    private final boolean encryption;

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final EventExecutorGroup cryptoGroup;
    private final EventExecutorGroup handlerGroup;

    public AtmServer(AtmServerProperties config, AtmServerListener listener) {
        encryption = !config.isCryptoDisable();

        bossGroup = TransportType.newEventLoopGroup(config.getBossThreads());
        workerGroup = TransportType.newEventLoopGroup(config.getWorkerThreads());

        cryptoGroup = encryption ? new DefaultEventExecutorGroup(config.getCryptoThreads()) : null;
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
                        if (encryption) {
                            pipeline.addLast(cryptoGroup, new CryptoDecoder());
                        }
                        pipeline.addLast(new MacDecoder());
                        pipeline.addLast(new HeaderDecoder());
                        pipeline.addLast(new AtmDecoder());

                        pipeline.addLast(new LengthPrepender());
                        if (encryption) {
                            pipeline.addLast(cryptoGroup, new CryptoEncoder());
                        }
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
        if (workerGroup != null) {
            LOG.info("Shutdown workerGroup");
            workerGroup.shutdownGracefully().sync().await();
        }
        if (bossGroup != null) {
            LOG.info("Shutdown bossGroup");
            bossGroup.shutdownGracefully().sync().await();
        }
        if (cryptoGroup != null) {
            LOG.info("Shutdown cryptoGroup");
            cryptoGroup.shutdownGracefully().sync().await();
        }
        if (handlerGroup != null) {
            LOG.info("Shutdown handlerGroup");
            handlerGroup.shutdownGracefully().sync().await();
        }
    }

}
