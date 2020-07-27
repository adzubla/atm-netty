package com.example.atm.netty.codec.atm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtmMessageTest {

    private static final String BODY = "abc";

    @Test
    void testId() {
        String id = "1234567";
        assertEquals(AtmMessage.ID_LENGTH, id.length());

        AtmMessage m = new AtmMessage(id, BODY);

        assertEquals(id, m.getId());
    }

    @Test
    void testIdPequeno() {
        String id = "1";
        AtmMessage m = new AtmMessage(id, BODY);

        String expected = "0000001";
        assertEquals(AtmMessage.ID_LENGTH, expected.length());

        assertEquals(expected, m.getId());
    }

    @Test
    void testIdGrande() {
        String idGrande = "12345678";
        assertTrue(idGrande.length() > AtmMessage.ID_LENGTH);

        assertThrows(IllegalArgumentException.class, () -> new AtmMessage(idGrande, BODY));
    }

    @Test
    void testIdNull() {
        assertThrows(IllegalArgumentException.class, () -> new AtmMessage(null, BODY));
    }

    @Test
    void testBodyNull() {
        assertThrows(IllegalArgumentException.class, () -> new AtmMessage("1234567", null));
    }

}
