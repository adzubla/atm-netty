package com.example.atm.netty.codec.crypto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CryptoDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        LOG.debug(">>> decode {} in={}, out={}", ctx, in, out);

        out.add(decrypt(in));
    }

    private ByteBuf decrypt(ByteBuf data) {
        return CryptoUtil.decrypt(data);
    }

}
