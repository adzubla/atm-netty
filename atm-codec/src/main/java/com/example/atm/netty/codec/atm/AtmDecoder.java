package com.example.atm.netty.codec.atm;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AtmDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(AtmDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        LOG.debug(">>> decode {} in={}, out={}", ctx, in, out);

        HeaderData headerData = ctx.channel().attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).get();

        String body = in.readCharSequence(in.readableBytes(), StandardCharsets.ISO_8859_1).toString();

        AtmMessage msg = new AtmMessage(headerData.getId(), body);

        out.add(msg);
    }

}
