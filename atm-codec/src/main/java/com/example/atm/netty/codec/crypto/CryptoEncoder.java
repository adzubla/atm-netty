package com.example.atm.netty.codec.crypto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        LOG.trace("<<< encode ctx={} msg={}, out={}", ctx, msg, out);

        out.writeBytes(encrypt(msg));
    }

    private ByteBuf encrypt(ByteBuf data) {
        return CryptoUtil.encrypt(data);
    }

}
