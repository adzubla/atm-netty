package com.example.atm.server.conn;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private final ConcurrentHashMap<ConnectionKey, ConnectionData> mapById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChannelHandlerContext, ConnectionKey> mapByCtx = new ConcurrentHashMap<>();

    public void add(ConnectionKey id, ChannelHandlerContext channelHandlerContext) {
        mapById.put(id, new ConnectionData(id, channelHandlerContext));
        mapByCtx.put(channelHandlerContext, id);
    }

    public ConnectionData get(ConnectionKey id) {
        return mapById.get(id);
    }

    public void remove(ChannelHandlerContext ctx) {
        LOG.debug("Removing {}", ctx);
        ConnectionKey id = mapByCtx.remove(ctx);
        mapById.remove(id);
        ctx.close();
    }

    public void remove(ConnectionKey id) {
        ConnectionData connectionData = mapById.get(id);
        if (connectionData != null) {
            remove(connectionData.channelHandlerContext);
        }
    }

    public Collection<ConnectionData> list() {
        return mapById.values();
    }

    @PreDestroy
    public void removeAll() {
        for (ChannelHandlerContext ctx : mapByCtx.keySet()) {
            remove(ctx);
        }
    }

    public static class ConnectionData {
        private final ConnectionKey key;
        private final Instant creationTime;
        private final ChannelHandlerContext channelHandlerContext;

        public ConnectionData(ConnectionKey key, ChannelHandlerContext channelHandlerContext) {
            this.key = key;
            this.creationTime = Instant.now();
            this.channelHandlerContext = channelHandlerContext;
        }

        public ConnectionKey getKey() {
            return key;
        }

        public Instant getCreationTime() {
            return creationTime;
        }

        public ChannelHandlerContext getChannelHandlerContext() {
            return channelHandlerContext;
        }

        public SocketAddress getRemoteAddress() {
            return channelHandlerContext.channel().remoteAddress();
        }

    }

}
