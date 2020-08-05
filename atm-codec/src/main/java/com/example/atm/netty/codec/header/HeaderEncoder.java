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
        LOG.debug("<<< encode ctx={} msg={}, out={}", ctx, msg, out);

        HeaderData headerData = ctx.channel().attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).get();
        if (headerData == null) {
            throw new IllegalStateException("HeaderData nao encontrado no pipeline");
        }

        // prepend header to output
        HeaderUtil.serialize(headerData, out);

        out.writeBytes(msg);
    }

}
