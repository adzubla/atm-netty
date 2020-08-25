package com.example.atm.netty.codec.header;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        LOG.trace("<<< encode ctx={} msg={}, out={}", ctx, msg, out);

        Long id = ctx.channel().attr(HeaderData.HEADER_ID_ATTRIBUTE_KEY).get();
        Byte type = ctx.channel().attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).get();
        if (type == null) {
            type = HeaderData.DATA;
        }

        HeaderData headerData = new HeaderData();
        headerData.setId(id);
        headerData.setTipo(type);

        // prepend header to output
        HeaderUtil.serialize(headerData, out);

        out.writeBytes(msg);
    }

}
