package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOG = LoggerFactory.getLogger(MacEncoder.class);

    public static final AttributeKey<Boolean> MAC_ENCODER_USE_ATTRIBUTE_KEY = AttributeKey.newInstance("MacEncoder.useMac");

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        LOG.trace("<<< encode ctx={} msg={}, out={}", ctx, msg, out);

        if (useMac(ctx)) {
            out.writeBytes(appendMac(msg));
        } else {
            LOG.trace("No MAC processing");
            out.writeBytes(msg);
        }
    }

    private boolean useMac(ChannelHandlerContext ctx) {
        Boolean use = ctx.channel().attr(MAC_ENCODER_USE_ATTRIBUTE_KEY).get();
        return use == null || use;
    }

    private ByteBuf appendMac(ByteBuf data) {
        ByteBuf mac = MacUtil.calculate(data);
        return data.writeBytes(mac);
    }

}
