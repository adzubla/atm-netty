package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MacUtil {

    public static final int MAC_LENGTH = 4;

    static ByteBuf calculate(ByteBuf data) {
        ByteBuf mac = Unpooled.buffer(MAC_LENGTH);

        // fake MAC
        mac.writeBytes(new byte[]{49, 50, 51, 52});

        return mac;
    }

}
