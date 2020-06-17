package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

import static com.example.atm.netty.codec.mac.MacUtil.MAC_LENGTH;

public class MacDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(processMac(in));
    }

    private ByteBuf processMac(ByteBuf data) {
        int length = data.readableBytes();

        ByteBuf body = data.readSlice(length - MAC_LENGTH);
        ByteBuf mac = data.readSlice(MAC_LENGTH);

        MacUtil.verifyMac(body, mac);

        return body.retain();
    }

}
