package com.example.atm.netty.codec.mac;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MacUtil {

    public static final int MAC_LENGTH = 32;

    public static ByteBuf calculate(ByteBuf data) {
        byte[] dataBytes = ByteBufUtil.getBytes(data);

        byte[] macBytes = calcula_mac_atm(dataBytes);

        String macHex = Hex.encodeHexString(macBytes, false);

        ByteBuf mac = Unpooled.buffer(MAC_LENGTH);
        mac.writeCharSequence(macHex, Charset.defaultCharset());

        return mac;
    }

    public static void verifyMac(ByteBuf data, ByteBuf mac) {
        byte[] dataBytes = ByteBufUtil.getBytes(data);
        byte[] macBytes;
        try {
            macBytes = Hex.decodeHex(new String(ByteBufUtil.getBytes(mac)));
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        byte[] actualMacBytes = MacUtil.calcula_mac_atm(dataBytes);

        if (Arrays.compare(macBytes, actualMacBytes) != 0) {
            throw new IllegalStateException("MAC nao confere");
        }
    }

    private static final byte[] CHAVEMAC = {0x2D, (byte) 0xDF, 0x1C, 0x3A, 0x42, 0x58, 0x71, (byte) 0xF3};
    private static final int TAM_MAX_CALCULO = 300;

    static byte[] calcula_mac_atm(byte[] msg) {

        int tam = msg.length;
        if (tam > TAM_MAX_CALCULO) {
            tam = TAM_MAX_CALCULO;
        }

        byte[] msg_aux = new byte[TAM_MAX_CALCULO];

        // Faz a copia dos TAM_MAX_CALCULO da mensagem original para o msg_aux
        //byte[] msg_aux = Arrays.copyOfRange(msg, 0, tam - 1);
        System.arraycopy(msg, 0, msg_aux, 0, tam);

        byte[] mac_comp = new byte[8];

        int fim = tam / 2;
        int i = 0, j = 0, k, num_max_operacoes;

        while (i < fim) {
            j = (((msg[i] | 0x88) + j + 1) << 3) % fim + fim;
            byte car = msg_aux[i];
            msg_aux[i] = msg_aux[j];
            msg_aux[j] = car;
            i++;
        }

        if (tam > 0) {
            num_max_operacoes = tam / 8;
            if (num_max_operacoes == 0) {
                num_max_operacoes = 1;
            }

            j = 0;
            k = 0;

            while (j < 8) {
                i = 0;
                do {
                    mac_comp[j] ^= msg_aux[k];
                    i++;
                    k++;
                } while (i < num_max_operacoes);

                mac_comp[j + 1] = (byte) (Byte.toUnsignedInt(mac_comp[j]) & 0x0F);
                mac_comp[j] = (byte) (Byte.toUnsignedInt(mac_comp[j]) >> 0x04);
                mac_comp[j] |= 0x30;

                if (mac_comp[j] > 0x39) {
                    mac_comp[j] += 0x07;
                }
                j++;

                mac_comp[j] |= 0x30;
                if (mac_comp[j] > 0x39) {
                    mac_comp[j] += 0x07;
                }
                j++;
            }
        }

        // Copia para "mac", o "mac_comp"
        byte[] mac = Arrays.copyOf(mac_comp, 8);

        // desnbs
        try {

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(CHAVEMAC, "DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(mac);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}
