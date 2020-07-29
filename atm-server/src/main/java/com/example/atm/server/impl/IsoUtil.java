package com.example.atm.server.impl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IsoUtil {

    private static final int MTI_SIZE = 4;
    private static final int BITMAP_SIZE = 8;
    private static final int BITMAP_STRING_SIZE = BITMAP_SIZE * 2;
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    public static byte[] compactBitmap(String data) {
        if (data == null || data.length() < MTI_SIZE + BITMAP_STRING_SIZE) {
            throw new IllegalArgumentException("Dados invalidos: " + data);
        }

        byte[] result = new byte[data.length() - (BITMAP_STRING_SIZE - BITMAP_SIZE)];

        // copia MTI
        byte[] mti = data.substring(0, MTI_SIZE).getBytes(StandardCharsets.ISO_8859_1);
        System.arraycopy(mti, 0, result, 0, MTI_SIZE);

        // compacta bitmap: hex ascii -> binario
        for (int i = MTI_SIZE; i < MTI_SIZE + BITMAP_STRING_SIZE; i += 2) {
            int c1 = Character.digit(data.charAt(i), 16);
            int c2 = Character.digit(data.charAt(i + 1), 16);
            int b = c1 * 16 + c2;

            result[(MTI_SIZE + i) / 2] = (byte) (b & 0xFF);
        }

        // concatena o resto da mensagem ao bitmap
        byte[] body = data.substring(MTI_SIZE + BITMAP_STRING_SIZE).getBytes(StandardCharsets.ISO_8859_1);
        System.arraycopy(body, 0, result, MTI_SIZE + BITMAP_SIZE, body.length);

        return result;
    }

    public static String expandBitmap(byte[] data) {
        if (data == null || data.length < MTI_SIZE + BITMAP_SIZE) {
            throw new IllegalArgumentException("Dados invalidos: " + Arrays.toString(data));
        }

        StringBuilder result = new StringBuilder(data.length + (BITMAP_STRING_SIZE - BITMAP_SIZE));

        // copia MTI
        String mti = new String(Arrays.copyOfRange(data, 0, MTI_SIZE), StandardCharsets.ISO_8859_1);
        result.append(mti);

        // expande bitmap: binario -> hex ascii
        for (int i = MTI_SIZE; i < MTI_SIZE + BITMAP_SIZE; i++) {
            int b = data[i] & 0xFF;

            result.append(HEX_DIGITS[b >>> 4]);
            result.append(HEX_DIGITS[b & 0x0F]);
        }

        // concatena o resto da mensagem ao bitmap
        String body = new String(Arrays.copyOfRange(data, MTI_SIZE + BITMAP_SIZE, data.length), StandardCharsets.ISO_8859_1);
        result.append(body);

        return result.toString();
    }

    public static String dump(String data) {
        if (data == null) {
            return "<null>";
        } else if (data.length() <= 20) {
            return "<" + data + ">";
        } else {
            return "<" + data.substring(0, 4) + " " + data.substring(4, 20) + "...>";
        }
    }

}
