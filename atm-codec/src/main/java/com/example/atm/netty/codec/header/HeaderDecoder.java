package com.example.atm.netty.codec.header;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class HeaderDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderDecoder.class);

    private static final byte[] PONG_BYTE_ARRAY = " PONG ".getBytes(StandardCharsets.ISO_8859_1);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        LOG.trace(">>> decode {} in={}, out={}", ctx, in, out);

        HeaderData headerData = HeaderUtil.deserialize(in);
        Long id = headerData.getId();
        Byte type = headerData.getTipo();

        LOG.trace("id = {} type = {}", id, type);

        ctx.channel().attr(HeaderData.HEADER_ID_ATTRIBUTE_KEY).set(id);

        if (headerData.getTipo() == HeaderData.PING) {
            ctx.channel().attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).set(HeaderData.PONG);

            in.skipBytes(in.readableBytes());

            AtmMessage message = new AtmMessage(id, PONG_BYTE_ARRAY);
            ctx.channel().writeAndFlush(message);
        } else {
            ctx.channel().attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).set(type);

            ByteBuf content = in.readSlice(in.readableBytes());
            ByteBuf result = content.retain();
            out.add(result);
        }
    }

}
