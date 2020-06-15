package com.example.atm.netty.codec.crypto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CryptoUtil {

    public static ByteBuf encrypt(ByteBuf data) {
        int length = data.readableBytes();
        ByteBuf result = Unpooled.buffer(length);

        while (length-- > 0) {
            char c = (char) data.readByte();
            result.writeByte(rot13(c));
        }

        return result;
    }

    public static ByteBuf decrypt(ByteBuf data) {
        int length = data.readableBytes();
        ByteBuf result = Unpooled.buffer(length);

        while (length-- > 0) {
            char c = (char) data.readByte();
            result.writeByte(rot13(c));
        }

        return result;
    }

    public static char rot13(char c) {
        if (c >= 'a' && c <= 'm') c += 13;
        else if (c >= 'A' && c <= 'M') c += 13;
        else if (c >= 'n' && c <= 'z') c -= 13;
        else if (c >= 'N' && c <= 'Z') c -= 13;
        return c;
    }

}
