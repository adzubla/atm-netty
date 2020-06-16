package com.example.atm.netty.codec.atm;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class AtmEncoder extends MessageToByteEncoder<AtmMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AtmMessage msg, ByteBuf out) throws Exception {
        HeaderData headerData = new HeaderData(msg.getId());
        ctx.channel().attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).set(headerData);

        out.writeBytes(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.getBody()), Charset.defaultCharset()));
    }

}
