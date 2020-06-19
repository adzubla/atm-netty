package com.example.atm.server.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportType {
    private static final Logger LOG = LoggerFactory.getLogger(TransportType.class);

    private static final Class<? extends EventLoopGroup> eventLoopGroupClass;
    private static final Class<? extends ServerChannel> serverChannelClass;

    static {
        if ("Linux".equals(System.getProperty("os.name"))) {
            LOG.info("Using EPOLL network transport");
            eventLoopGroupClass = EpollEventLoopGroup.class;
            serverChannelClass = EpollServerSocketChannel.class;
        } else {
            LOG.info("Using NIO network transport");
            eventLoopGroupClass = NioEventLoopGroup.class;
            serverChannelClass = NioServerSocketChannel.class;
        }
    }

    public static EventLoopGroup newEventLoopGroup(int nThreads) {
        try {
            return eventLoopGroupClass.getDeclaredConstructor(Integer.TYPE).newInstance(nThreads);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<? extends ServerChannel> getServerChannelClass() {
        return serverChannelClass;
    }

}
