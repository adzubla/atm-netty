package com.example.atm.netty.codec.atm;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AtmDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(AtmDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        LOG.trace(">>> decode {} in={}, out={}", ctx, in, out);

        Long id = ctx.channel().attr(HeaderData.HEADER_ID_ATTRIBUTE_KEY).get();

        byte[] body = new byte[in.readableBytes()];
        in.readBytes(body);

        AtmMessage msg = new AtmMessage(id, body);

        out.add(msg);
    }

}
