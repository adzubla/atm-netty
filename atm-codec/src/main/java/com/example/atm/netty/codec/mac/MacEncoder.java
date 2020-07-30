package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOG = LoggerFactory.getLogger(MacEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        if (mustProcess(msg)) {
            out.writeBytes(appendMac(msg));
        } else {
            LOG.debug("No MAC processing");
            out.writeBytes(msg);
        }
    }

    private boolean mustProcess(ByteBuf data) {
        return data.getByte(50) != 120;
    }

    private ByteBuf appendMac(ByteBuf data) {
        ByteBuf mac = MacUtil.calculate(data);
        return data.writeBytes(mac);
    }

}
