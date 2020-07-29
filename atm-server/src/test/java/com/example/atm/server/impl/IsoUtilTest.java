package com.example.atm.server.impl;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class IsoUtilTest {

    @Test
    void testBitmap() {
        String m = "01020304050607FFxyz";

        byte[] byteArray = IsoUtil.compactBitmap(m);
        assert byteArray.length == 11;

        System.out.println("byteArray = " + Arrays.toString(byteArray));
        System.out.println("  as ints = " + Arrays.toString(toIntArray(byteArray)));

        String s = IsoUtil.expandBitmap(byteArray);

        System.out.println("m = " + m);
        System.out.println("s = " + s);

        assert m.equals(s);
    }

    private int[] toIntArray(byte[] b) {
        int[] a = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            a[i] = b[i] & 0xFF;
        }
        return a;
    }

}