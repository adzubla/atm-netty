package com.example.atm.netty.codec.header;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderDataTest {

    @Test
    void testId() {
        String id = "123456789012";
        assertEquals(HeaderData.ID_LENGTH, id.length());

        HeaderData h = new HeaderData(id);

        assertEquals(id, h.getId());
    }

    @Test
    void testIdPequeno() {
        String id = "1234567";
        HeaderData h = new HeaderData(id);

        String expected = "000001234567";
        assertEquals(HeaderData.ID_LENGTH, expected.length());

        assertEquals(expected, h.getId());
    }

    @Test
    void testIdGrande() {
        String idGrande = "1234567890123";
        assertTrue(idGrande.length() > HeaderData.ID_LENGTH);

        assertThrows(IllegalArgumentException.class, () -> {
                    new HeaderData(idGrande);
                }
        );
    }

    @Test
    void testIdNull() {
        assertThrows(IllegalArgumentException.class, () -> {
                    new HeaderData(null);
                }
        );
    }

    @Test
    void testLocalidadeIdTerminal() {
        String localidade = "012";
        String idTerminal = "0987";

        String expected = "000000120987";
        assertEquals(HeaderData.ID_LENGTH, expected.length());

        HeaderData h = new HeaderData(localidade, idTerminal);

        assertEquals(expected, h.getId());
        assertEquals(localidade, h.getLocalidade());
        assertEquals(idTerminal, h.getIdTerminal());
    }

    @Test
    void testLocalidadeInvalida() {
        String localidade = "12";
        String idTerminal = "0987";
        assertThrows(IllegalArgumentException.class, () -> {
                    new HeaderData(localidade, idTerminal);
                }
        );
    }

    @Test
    void testIdTerminalInvalid0() {
        String localidade = "012";
        String idTerminal = "987";
        assertThrows(IllegalArgumentException.class, () -> {
                    new HeaderData(localidade, idTerminal);
                }
        );
    }

}
