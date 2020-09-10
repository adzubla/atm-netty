package com.example.atm.netty.codec.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IsoUtilTest {

    @Test
    void testBitmap() {
        String m = "01107F010203040506FFxyz";

        byte[] byteArray = IsoUtil.compactBitmap(m);

        System.out.println("byteArray = " + Arrays.toString(byteArray));
        System.out.println("  as ints = " + Arrays.toString(toIntArray(byteArray)));

        assertEquals(15, byteArray.length);

        String s = IsoUtil.expandBitmap(byteArray);

        System.out.println("m = " + m);
        System.out.println("s = " + s);

        assertEquals(m, s);
    }

    private int[] toIntArray(byte[] b) {
        int[] a = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            a[i] = b[i] & 0xFF;
        }
        return a;
    }

}