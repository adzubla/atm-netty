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

    private final ConcurrentHashMap<ConnectionKey, ConnectionData> mapByKey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ChannelHandlerContext, ConnectionData> mapByCtx = new ConcurrentHashMap<>();

    public void add(ChannelHandlerContext ctx) {
        ConnectionData data = new ConnectionData(ctx);
        mapByCtx.put(ctx, data);
    }

    public void add(String id, ChannelHandlerContext ctx) {
        ConnectionKey key = new ConnectionKey(id);
        ConnectionData data = mapByCtx.get(ctx);
        if (data.getKey() == null) {
            data.setKey(key);
            ConnectionData prev = mapByKey.put(key, data);
            if (prev != null) {
                prev.channelHandlerContext.close();
            }
        }
        data.countInput();
    }

    public ConnectionData get(String id) {
        return mapByKey.get(new ConnectionKey(id));
    }

    public Collection<ConnectionData> list() {
        return mapByCtx.values();
    }

    public void remove(ChannelHandlerContext ctx) {
        LOG.debug("Removing {}", ctx);
        ConnectionData data = mapByCtx.remove(ctx);
        if (data != null && data.getKey() != null) {
            mapByKey.remove(data.getKey());
        }
        ctx.close();
    }

    public void remove(String id) {
        ConnectionData data = mapByKey.get(new ConnectionKey(id));
        if (data != null) {
            remove(data.channelHandlerContext);
        }
    }

    @PreDestroy
    public void removeAll() {
        for (ChannelHandlerContext ctx : mapByCtx.keySet()) {
            remove(ctx);
        }
    }

    public static class ConnectionData {
        private final Instant creationTime;
        private final ChannelHandlerContext channelHandlerContext;

        private ConnectionKey key;
        private Instant lastInputTime;
        private Instant lastOutputTime;
        private long inputCount;
        private long outputCount;
        private long lastResponseDuration;
        private long totalResponseDuration;
        private long minResponseDuration = Long.MAX_VALUE;
        private long maxResponseDuration;

        public ConnectionData(ChannelHandlerContext channelHandlerContext) {
            this.creationTime = Instant.now();
            this.channelHandlerContext = channelHandlerContext;
        }

        public ConnectionKey getKey() {
            return key;
        }

        public void setKey(ConnectionKey key) {
            this.key = key;
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

        public SocketAddress getRemoteAddress() {
            return channelHandlerContext.channel().remoteAddress();
        }

        public ChannelHandlerContext getChannelHandlerContext() {
            return channelHandlerContext;
        }

    }

}
