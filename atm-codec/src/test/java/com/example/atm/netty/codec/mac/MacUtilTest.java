package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MacUtilTest {

    final String dataString = "3930303041303030" + "3034303142383835"
            + "3130304530303030" + "3030303031303030"
            + "3030353333303230" + "3030303431313130"
            + "3030303132343732" + "3030313130303030"
            + "3030303031323433" + "3830313234373230"
            + "3030303430303030" + "303D303031313030"
            + "3030303030303030" + "3030303030303031"
            + "3034303030303030" + "3030303030303030"
            + "3030303030303030" + "3030303030303030"
            + "3030303030303030" + "3030303030303030"
            + "3030303030303030" + "3030303030303030"
            + "3030303030303030" + "3030303030303030"
            + "3030303030303030" + "3030303030303030"
            + "3030303030303030" + "3030303030303037"
            + "3133303531393037" + "3833313738333031"
            + "3530303030303130" + "3030303031303030"
            + "3030343030333135" + "4546313546333638"
            + "4535383442343730" + "3039433130343531"
            + "3030383035343030" + "4E4E333030593031"
            + "3034303030303031" + "3031323430313234"
            + "3732303030303430" + "3030303030303037"
            + "3030303030573030" + "3030313830313030"
            + "3130303030303032" + "3031313030303130"
            + "3030303030303030" + "3830373030313230"
            + "3130303231303030" + "3930303030303030"
            + "3030";

    final String expectedMacString = "AFAC56F7F14EC8C8276E72185B232F9E";

    @Test
    void test_calcula_mac_atm() throws DecoderException {
        byte[] expectedMacBytes = Hex.decodeHex(expectedMacString);
        byte[] dataBytes = Hex.decodeHex(dataString);

        byte[] macBytes = MacUtil.calcula_mac_atm(dataBytes);

        assertEquals(MacUtil.MAC_LENGTH / 2, macBytes.length);
        assertArrayEquals(expectedMacBytes, macBytes);
    }

    @Test
    void testCalculate() throws DecoderException {
        ByteBuf expectedMac = Unpooled.buffer(expectedMacString.length());
        expectedMac.writeCharSequence(expectedMacString, StandardCharsets.ISO_8859_1);

        ByteBuf data = Unpooled.buffer(dataString.length());
        data.writeBytes(Hex.decodeHex(dataString));

        ByteBuf mac = MacUtil.calculate(data);

        assertEquals(MacUtil.MAC_LENGTH, mac.readableBytes());
        assertEquals(0, ByteBufUtil.compare(expectedMac, mac));
    }

    @Test
    void testVerifyOk() throws DecoderException {
        ByteBuf expectedMac = Unpooled.buffer(expectedMacString.length());
        expectedMac.writeCharSequence(expectedMacString, StandardCharsets.ISO_8859_1);

        ByteBuf data = Unpooled.buffer(dataString.length());
        data.writeBytes(Hex.decodeHex(dataString));

        MacUtil.verifyMac(data, expectedMac);
    }

    @Test
    void testVerifyFail() throws DecoderException {
        ByteBuf expectedMac = Unpooled.buffer(expectedMacString.length());
        expectedMac.writeCharSequence(expectedMacString, StandardCharsets.ISO_8859_1);

        String tamperedContent = dataString.substring(2);
        ByteBuf data = Unpooled.buffer(tamperedContent.length());
        data.writeBytes(Hex.decodeHex(tamperedContent));

        assertThrows(IllegalStateException.class, () -> MacUtil.verifyMac(data, expectedMac));
    }

    @Test
    void testErro() throws DecoderException {
        MacUtil.calcula_mac_atm(Hex.decodeHex("01"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102030405"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203040506"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304050607"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102030405060708"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203040506070809"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304050607080901"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102030405060708090102"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203040506070809010203"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304050607080901020304"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102030405060708090102030405"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203040506070809010203040506"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304050607080901020304050607"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102030405060708090102030405060708"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203040506070809010203040506070809"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304050607080901020304050607080901"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("010203040506070809100102030405060708090102030405060708090102"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("01020304050607080910010203040506070809010203040506070809010203"));
        MacUtil.calcula_mac_atm(Hex.decodeHex("0102030405060708091001020304050607080901020304050607080901020304"));
    }

}
