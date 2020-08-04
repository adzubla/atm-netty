package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MacDecoderTest {

    public ByteBuf createBuffer(String hexStr) {
        ByteBuf buf = Unpooled.buffer(hexStr.length() / 2);
        buf.writeBytes(ByteBufUtil.decodeHexDump(hexStr));
        return buf;
    }

    @Test
    void testWithMac() {
        String binStr = "01010503013030303030303030333333" +
                "33000000000000000000000000003132" +
                "33343232313030303131303243303438" +
                "30356162634331333235383331354544" +
                "38443943413237364537323138354232" +
                "3332463945";

        ByteBuf buf = createBuffer(binStr);
        MacDecoder decoder = new MacDecoder();

        assertTrue(decoder.hasMac(buf));
    }

    @Test
    void testWithoutMac() {
        String binStr = "01010503013030303030303030333333" +
                "33000000000000000000000000003132" +
                "33343232313030303131303243303438" +
                "303478797a";

        ByteBuf buf = createBuffer(binStr);
        MacDecoder decoder = new MacDecoder();

        assertFalse(decoder.hasMac(buf));
    }

}