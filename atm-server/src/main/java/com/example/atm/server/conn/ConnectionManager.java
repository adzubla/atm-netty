package com.example.atm.server.conn;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private final ConcurrentHashMap<ConnectionKey, ConnectionData> mapById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChannelHandlerContext, ConnectionKey> mapByCtx = new ConcurrentHashMap<>();

    public void add(ConnectionKey key, ChannelHandlerContext ctx) {
        ConnectionData data = mapById.get(key);
        if (data != null) {
            data.countInput();
        } else {
            data = new ConnectionData(key, ctx);
            data.countInput();
            mapById.put(key, data);
            mapByCtx.put(ctx, key);
        }
    }

    public ConnectionData get(ConnectionKey key) {
        return mapById.get(key);
    }

    public Collection<ConnectionData> list() {
        return mapById.values();
    }

    public void remove(ChannelHandlerContext ctx) {
        LOG.debug("Removing {}", ctx);
        ConnectionKey key = mapByCtx.remove(ctx);
        if (key != null) {
            mapById.remove(key);
        }
        ctx.close();
    }

    public void remove(ConnectionKey key) {
        ConnectionData connectionData = mapById.get(key);
        if (connectionData != null) {
            remove(connectionData.channelHandlerContext);
        }
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

        private Instant lastInputTime;
        private Instant lastOutputTime;
        private long inputCount;
        private long outputCount;
        private long lastResponseDuration;
        private long totalResponseDuration;
        private long minResponseDuration = Long.MAX_VALUE;
        private long maxResponseDuration;

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

        public Instant getLastInputTime() {
            return lastInputTime;
        }

        public void countInput() {
            inputCount++;
            lastInputTime = Instant.now();
        }

        public Instant getLastOutputTime() {
            return lastOutputTime;
        }

        public void countOutput() {
            outputCount++;
            lastOutputTime = Instant.now();
            if (lastOutputTime.isAfter(lastInputTime)) {
                long t = Duration.between(lastInputTime, lastOutputTime).toMillis();
                totalResponseDuration += t;
                lastResponseDuration = t;
                if (t < minResponseDuration) {
                    minResponseDuration = t;
                }
                if (t > maxResponseDuration) {
                    maxResponseDuration = t;
                }
            }
        }

        public long getInputCount() {
            return inputCount;
        }

        public long getOutputCount() {
            return outputCount;
        }

        public long getLastResponseDuration() {
            return lastResponseDuration;
        }

        public long getAverageResponseDuration() {
            return outputCount == 0 ? 0 : totalResponseDuration / outputCount;
        }

        public long getMinResponseDuration() {
            return minResponseDuration;
        }

        public long getMaxResponseDuration() {
            return maxResponseDuration;
        }

        public ChannelHandlerContext getChannelHandlerContext() {
            return channelHandlerContext;
        }

        public SocketAddress getRemoteAddress() {
            return channelHandlerContext.channel().remoteAddress();
        }

    }

}
