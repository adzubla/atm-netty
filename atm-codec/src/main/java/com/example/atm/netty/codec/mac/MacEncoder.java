package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MacEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeBytes(appendMac(msg));
    }

    private ByteBuf appendMac(ByteBuf data) {
        ByteBuf mac = MacUtil.calculate(data);
        return data.writeBytes(mac);
    }

}
