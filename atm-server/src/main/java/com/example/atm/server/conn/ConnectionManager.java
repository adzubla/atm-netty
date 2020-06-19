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
        if (data.key == null) {
            data.key = key;
            ConnectionData prev = mapByKey.put(key, data);
            if (prev != null) {
                mapByCtx.remove(prev.context);
                prev.context.close();
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
        if (data != null && data.key != null) {
            mapByKey.remove(data.key);
        }
        ctx.close();
    }

    public void remove(String id) {
        ConnectionData data = mapByKey.get(new ConnectionKey(id));
        if (data != null) {
            remove(data.context);
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
        private final ChannelHandlerContext context;

        private ConnectionKey key;
        private Instant lastInputTime;
        private Instant lastOutputTime;
        private long inputCount;
        private long outputCount;
        private long totalResponseDuration;
        private Long lastResponseDuration;
        private Long minResponseDuration;
        private Long maxResponseDuration;

        public ConnectionData(ChannelHandlerContext context) {
            this.creationTime = Instant.now();
            this.context = context;
        }

        public ChannelHandlerContext channelHandlerContext() {
            return context;
        }

        public void countInput() {
            inputCount++;
            lastInputTime = Instant.now();
        }

        public void countOutput() {
            outputCount++;
            lastOutputTime = Instant.now();

            if (lastOutputTime.isAfter(lastInputTime)) {
                long t = Duration.between(lastInputTime, lastOutputTime).toMillis();
                totalResponseDuration += t;
                lastResponseDuration = t;

                if (minResponseDuration == null || t < minResponseDuration) {
                    minResponseDuration = t;
                }
                if (maxResponseDuration == null || t > maxResponseDuration) {
                    maxResponseDuration = t;
                }
            }
        }

        public String getId() {
            return key == null ? null : key.getId();
        }

        public SocketAddress getRemoteAddress() {
            return context.channel().remoteAddress();
        }

        public Instant getCreationTime() {
            return creationTime;
        }

        public Instant getLastInputTime() {
            return lastInputTime;
        }

        public Instant getLastOutputTime() {
            return lastOutputTime;
        }

        public long getInputCount() {
            return inputCount;
        }

        public long getOutputCount() {
            return outputCount;
        }

        public Long getLastResponseDuration() {
            return lastResponseDuration;
        }

        public Long getAverageResponseDuration() {
            return outputCount == 0 ? null : totalResponseDuration / outputCount;
        }

        public Long getMinResponseDuration() {
            return minResponseDuration;
        }

        public Long getMaxResponseDuration() {
            return maxResponseDuration;
        }

    }

}
