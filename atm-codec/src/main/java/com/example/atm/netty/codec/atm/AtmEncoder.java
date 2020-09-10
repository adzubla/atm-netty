package com.example.atm.netty.codec.atm;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmEncoder extends MessageToByteEncoder<AtmMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(AtmEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, AtmMessage msg, ByteBuf out) {
        LOG.trace("<<< encode ctx={} msg={}, out={}", ctx, msg, out);

        ctx.channel().attr(HeaderData.HEADER_ID_ATTRIBUTE_KEY).set(msg.getId());

        out.writeBytes(msg.getBody());
    }

}
