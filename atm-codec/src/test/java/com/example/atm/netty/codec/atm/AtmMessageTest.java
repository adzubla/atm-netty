package com.example.atm.netty.codec.atm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtmMessageTest {

    private static final String BODY = "abc";

    @Test
    void testId() {
        String id = "123456789012";
        assertEquals(AtmMessage.ID_LENGTH, id.length());

        AtmMessage m = new AtmMessage(id, BODY);

        assertEquals(id, m.getId());
    }

    @Test
    void testIdPequeno() {
        String id = "1234567";
        AtmMessage m = new AtmMessage(id, BODY);

        String expected = "000001234567";
        assertEquals(AtmMessage.ID_LENGTH, expected.length());

        assertEquals(expected, m.getId());
    }

    @Test
    void testIdGrande() {
        String idGrande = "1234567890123";
        assertTrue(idGrande.length() > AtmMessage.ID_LENGTH);

        assertThrows(IllegalArgumentException.class, () -> {
                    new AtmMessage(idGrande, BODY);
                }
        );
    }

    @Test
    void testIdNull() {
        assertThrows(IllegalArgumentException.class, () -> {
                    AtmMessage m = new AtmMessage(null, BODY);
                }
        );
    }

    @Test
    void testBodyNull() {
        assertThrows(IllegalArgumentException.class, () -> {
                    new AtmMessage("123456789012", null);
                }
        );
    }

}
