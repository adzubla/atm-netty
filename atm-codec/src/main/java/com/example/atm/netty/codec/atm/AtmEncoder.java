package com.example.atm.netty.codec.atm;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class AtmEncoder extends MessageToByteEncoder<AtmMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(AtmEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, AtmMessage msg, ByteBuf out) {
        LOG.debug("<<< encode ctx={} msg={}, out={}", ctx, msg, out);

        ctx.channel().attr(HeaderData.HEADER_ID_ATTRIBUTE_KEY).set(msg.getId());
        ctx.channel().attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).set(HeaderData.DATA);

        out.writeBytes(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.getBody()), StandardCharsets.ISO_8859_1));
    }

}
