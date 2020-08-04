package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.example.atm.netty.codec.mac.MacUtil.MAC_LENGTH;

public class MacDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(MacDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (hasMac(in)) {
            out.add(processMac(in));
        } else {
            LOG.debug("No MAC processing");
            out.add(in.readBytes(in.readableBytes()));
        }
    }

    protected boolean hasMac(ByteBuf data) {
        int firstBytePos = 34; // posicao no buffer do primeiro "byte" do bitmap
        int firstByte = data.getByte(firstBytePos) & 0xFF;
        int firstBit = firstByte & 0b1000;

        int len = (firstBit == 0) ? 16 : 32; // o bitmap pode ter 64 ou 128 bits, entao ajustamos o tamanho

        int lastBytePos = firstBytePos + len - 1; // posicao no buffer do ultimo "byte" do bitmap
        int lastByte = data.getByte(lastBytePos) & 0xFF;
        int lastBit = lastByte & 1;

        return lastBit != 0; // utimo bit indica se o MAC esta presente
    }

    protected ByteBuf processMac(ByteBuf data) {
        int length = data.readableBytes();

        ByteBuf body = data.readSlice(length - MAC_LENGTH);
        ByteBuf mac = data.readSlice(MAC_LENGTH);

        try {
            MacUtil.verifyMac(body, mac);
        } catch (IllegalStateException e) {
            LOG.warn(e.toString());
        }

        return body.retain();
    }

}
