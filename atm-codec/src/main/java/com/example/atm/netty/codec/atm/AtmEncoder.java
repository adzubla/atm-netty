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

        HeaderData headerData = new HeaderData();
        headerData.setId(atmIdToHeaderDataId(msg.getId()));

        ctx.channel().attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).set(headerData);

        out.writeBytes(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.getBody()), StandardCharsets.ISO_8859_1));
    }


    private String atmIdToHeaderDataId(String atmId) {
        if (atmId == null || atmId.length() > HeaderData.HEADER_ID_LENGTH) {
            throw new IllegalArgumentException("id invalido: " + atmId);
        } else if (atmId.length() < HeaderData.HEADER_ID_LENGTH) {
            StringBuilder sb = new StringBuilder("000000000000");
            return sb.substring(atmId.length()) + atmId;
        } else {
            return atmId;
        }
    }

}
