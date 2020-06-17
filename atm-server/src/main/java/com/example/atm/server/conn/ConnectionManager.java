package com.example.atm.server.conn;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {

    private final ConcurrentHashMap<ConnectionId, ConnectionData> mapById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChannelHandlerContext, ConnectionId> mapByCtx = new ConcurrentHashMap<>();

    public void add(ConnectionId id, ChannelHandlerContext channelHandlerContext) {
        mapById.put(id, new ConnectionData(id, channelHandlerContext));
        mapByCtx.put(channelHandlerContext, id);
    }

    public ConnectionData get(ConnectionId id) {
        return mapById.get(id);
    }

    public void remove(ChannelHandlerContext ctx) {
        ConnectionId id = mapByCtx.remove(ctx);
        mapById.remove(id);
    }

    public Collection<ConnectionData> list() {
        return mapById.values();
    }

    public static class ConnectionData {
        private final ConnectionId id;
        private final Instant creationTime;
        private final ChannelHandlerContext channelHandlerContext;

        public ConnectionData(ConnectionId id, ChannelHandlerContext channelHandlerContext) {
            this.id = id;
            this.creationTime = Instant.now();
            this.channelHandlerContext = channelHandlerContext;
        }

        public ConnectionId getId() {
            return id;
        }

        public Instant getCreationTime() {
            return creationTime;
        }

        public ChannelHandlerContext getChannelHandlerContext() {
            return channelHandlerContext;
        }

        public String getName() {
            return channelHandlerContext.name();
        }

        public SocketAddress getRemoteAddress() {
            return channelHandlerContext.channel().remoteAddress();
        }
    }

}
