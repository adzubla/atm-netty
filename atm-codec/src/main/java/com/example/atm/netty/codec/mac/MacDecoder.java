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
        LOG.trace(">>> decode {} in={}, out={}", ctx, in, out);

        LOG.trace("readableBytes = {}", in.readableBytes());
        if (in.readableBytes() > 62 && hasMac(in)) {
            try {
                out.add(processMac(in));
            } catch (Exception e) {
                LOG.warn("Discarding message, MAC error: " + e);
            }
        } else {
            LOG.trace("No MAC processing");
            out.add(in.readBytes(in.readableBytes()));
        }
    }

    protected boolean hasMac(ByteBuf data) {
        int firstBytePos = 34; // posicao no buffer do primeiro "byte" do bitmap
        int firstByte = data.getByte(firstBytePos) & 0xFF;

        // Analisando o mapa de bits descompactado.
        // Portanto, ira chegar um caracter hexa correspondente ao nibble mais significativo
        // Se c < 0x38, bit 0 nao esta setado.  Caso contrario, esta setado.
        int len = (firstByte < 0x38) ? 16 : 32; // o bitmap pode ter 64 ou 128 bits, entao ajustamos o tamanho

        int lastBytePos = firstBytePos + len - 1; // posicao no buffer do ultimo "byte" do bitmap

        // Para tratar ultimo nibble, primeiro o compacta
        int lastByte = Character.digit(data.getByte(lastBytePos), 16);
        int lastBit = lastByte & 1;

        return lastBit != 0; // ultimo bit indica se o MAC esta presente
    }

    protected ByteBuf processMac(ByteBuf data) {
        int length = data.readableBytes();

        ByteBuf body = data.readSlice(length - MAC_LENGTH);
        ByteBuf mac = data.readSlice(MAC_LENGTH);

        MacUtil.verifyMac(body, mac);

        return body.retain();
    }

}
