package com.example.atm.netty.codec.header;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HeaderDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        LOG.debug(">>> decode {} in={}, out={}", ctx, in, out);

        out.add(processHeader(in, ctx));
    }

    private ByteBuf processHeader(ByteBuf data, ChannelHandlerContext ctx) {
        HeaderData headerData = HeaderUtil.deserialize(data);
        ctx.channel().attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).set(headerData);

        ByteBuf content = data.readSlice(data.readableBytes());

        return content.retain();
    }

}
