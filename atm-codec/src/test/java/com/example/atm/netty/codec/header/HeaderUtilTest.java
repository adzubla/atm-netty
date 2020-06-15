package com.example.atm.netty.codec.header;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderUtilTest {

    public static final byte[] MESSAGE_ARRAY = new byte[]{1, 1, 5, 3, 1, 48, 48, 48, 48, 48, 48, 49, 50, 48, 57, 56, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final byte[] THIRTEEN_ZEROES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public ByteBuf messageBuf;

    @BeforeEach
    void setup() {
        messageBuf = Unpooled.buffer(HeaderUtil.HEADER_LENGTH);
        messageBuf.writeBytes(MESSAGE_ARRAY);
        assertEquals(HeaderUtil.HEADER_LENGTH, messageBuf.readableBytes());
    }

    @Test
    void testSerialize() {
        HeaderData headerData = new HeaderData("000000120987");
        ByteBuf out = Unpooled.buffer(HeaderUtil.HEADER_LENGTH);

        HeaderUtil.serialize(headerData, out);

        assertEquals(HeaderUtil.HEADER_LENGTH, out.readableBytes());
        assertEquals(0, ByteBufUtil.compare(messageBuf, out));
    }

    @Test
    void testDeserialize() {
        HeaderData h = HeaderUtil.deserialize(messageBuf);
        assertEquals(1, h.getVersao());
        assertEquals(1, h.getFormato());
        assertEquals(5, h.getServico());
        assertEquals(3, h.getTipo());
        assertEquals(1, h.getFormatoId());
        assertEquals("000000120987", h.getId());
        assertEquals("012", h.getLocalidade());
        assertEquals("0987", h.getIdTerminal());
        assertArrayEquals(THIRTEEN_ZEROES, h.getReservado());
    }

}