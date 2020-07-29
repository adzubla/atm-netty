package com.example.atm.server.impl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IsoUtil {

    private static final int BITMAP_SIZE = 8;
    private static final int BITMAP_STRING_SIZE = BITMAP_SIZE * 2;
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    public static byte[] compactBitmap(String data) {
        if (data == null || data.length() < BITMAP_STRING_SIZE) {
            throw new IllegalArgumentException("Dados invalidos: " + data);
        }

        byte[] result = new byte[data.length() - (BITMAP_STRING_SIZE - BITMAP_SIZE)];

        // compacta bitmap: hex ascii -> binario
        for (int i = 0; i < BITMAP_STRING_SIZE; i += 2) {
            int c1 = Character.digit(data.charAt(i), 16);
            int c2 = Character.digit(data.charAt(i + 1), 16);
            int b = c1 * 16 + c2;

            result[i / 2] = (byte) (b & 0xFF);
        }

        // concatena o resto da mensagem ao bitmap
        byte[] body = data.substring(BITMAP_STRING_SIZE).getBytes(StandardCharsets.ISO_8859_1);
        System.arraycopy(body, 0, result, BITMAP_SIZE, body.length);

        return result;
    }

    public static String expandBitmap(byte[] data) {
        if (data == null || data.length < BITMAP_SIZE) {
            throw new IllegalArgumentException("Dados invalidos: " + Arrays.toString(data));
        }

        StringBuilder result = new StringBuilder(data.length + (BITMAP_STRING_SIZE - BITMAP_SIZE));

        // expande bitmap: binario -> hex ascii
        for (int i = 0; i < BITMAP_SIZE; i++) {
            int b = data[i] & 0xFF;

            result.append(HEX_DIGITS[b >>> 4]);
            result.append(HEX_DIGITS[b & 0x0F]);
        }

        // concatena o resto da mensagem ao bitmap
        String body = new String(Arrays.copyOfRange(data, BITMAP_SIZE, data.length), StandardCharsets.ISO_8859_1);
        result.append(body);

        return result.toString();
    }

}
