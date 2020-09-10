package com.example.atm.netty.codec.util;

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

        // Analisando o mapa de bits descompactado.
        // Portanto, ira chegar um caracter hexa correspondente ao nibble mais significativo (h)
        // Se a conversão de h para base 10 < 8, bit 0 nao esta setado.
        // Caso contrario, esta setado.
        // Se estiver setado, existem 2 mapas de 64 bits
        int totBitMapSize = BITMAP_SIZE;
        int totBitMapStringSize = BITMAP_STRING_SIZE;
        if (Character.digit(data.charAt(MTI_SIZE), 16) >= 0x08) {
            totBitMapSize *= 2;
            totBitMapStringSize *= 2;
        }

        byte[] result = new byte[data.length() - (totBitMapStringSize - totBitMapSize)];

        // copia MTI
        byte[] mti = data.substring(0, MTI_SIZE).getBytes(StandardCharsets.ISO_8859_1);
        System.arraycopy(mti, 0, result, 0, MTI_SIZE);

        // compacta bitmap: hex ascii -> binario
        for (int i = MTI_SIZE; i < MTI_SIZE + totBitMapStringSize; i += 2) {
            int c1 = Character.digit(data.charAt(i), 16);
            int c2 = Character.digit(data.charAt(i + 1), 16);
            int b = c1 * 16 + c2;

            result[(MTI_SIZE + i) / 2] = (byte) (b & 0xFF);
        }

        // concatena o resto da mensagem ao bitmap
        byte[] body = data.substring(MTI_SIZE + totBitMapStringSize).getBytes(StandardCharsets.ISO_8859_1);
        System.arraycopy(body, 0, result, MTI_SIZE + totBitMapSize, body.length);

        return result;
    }

    public static String expandBitmap(byte[] data) {
        if (data == null || data.length < MTI_SIZE + BITMAP_SIZE) {
            throw new IllegalArgumentException("Dados invalidos: " + Arrays.toString(data));
        }

        // Analisando o mapa de bits compactado.
        // Portanto, basta testar bit mais significativo de primeiro mapa.
        // Se estiver setado, existem 2 mapas de 64 bits
        int totBitMapSize = BITMAP_SIZE;
        int totBitMapStringSize = BITMAP_STRING_SIZE;

        int firstByte = (int) data[MTI_SIZE] & 0X00FF;
        if ((firstByte & 0x80) > 0) {
            totBitMapSize *= 2;
            totBitMapStringSize *= 2;
        }

        StringBuilder result = new StringBuilder(data.length + (totBitMapStringSize - totBitMapSize));

        // copia MTI
        String mti = new String(Arrays.copyOfRange(data, 0, MTI_SIZE), StandardCharsets.ISO_8859_1);
        result.append(mti);

        // expande bitmap: binario -> hex ascii
        for (int i = MTI_SIZE; i < MTI_SIZE + totBitMapSize; i++) {
            int b = data[i] & 0xFF;

            result.append(HEX_DIGITS[b >> 4]);
            result.append(HEX_DIGITS[b & 0x0F]);
        }

        // concatena o resto da mensagem ao bitmap
        String body = new String(Arrays.copyOfRange(data, MTI_SIZE + totBitMapSize, data.length), StandardCharsets.ISO_8859_1);
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