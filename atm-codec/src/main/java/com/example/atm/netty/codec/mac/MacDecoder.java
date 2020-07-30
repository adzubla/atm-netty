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
        if (mustProcess(in)) {
            out.add(processMac(in));
        } else {
            LOG.debug("No MAC processing");
            out.add(in.readBytes(in.readableBytes()));
        }
    }

    private boolean mustProcess(ByteBuf data) {
        return data.getByte(50) != 120;
    }

    private ByteBuf processMac(ByteBuf data) {
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
