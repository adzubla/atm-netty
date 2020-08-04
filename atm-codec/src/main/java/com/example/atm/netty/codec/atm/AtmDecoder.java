package com.example.atm.netty.codec.atm;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AtmDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        HeaderData headerData = ctx.channel().attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).get();

        String headerDataId = headerData.getId();
        String body = in.readCharSequence(in.readableBytes(), StandardCharsets.ISO_8859_1).toString();

        AtmMessage msg = new AtmMessage(headerIdToAtmId(headerDataId), body);
        out.add(msg);
    }

    private String headerIdToAtmId(String headerDataId) {
        return headerDataId.substring(headerDataId.length() - AtmMessage.ID_LENGTH);
    }

}
