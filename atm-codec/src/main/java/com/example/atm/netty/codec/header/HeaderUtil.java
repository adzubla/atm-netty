package com.example.atm.netty.codec.header;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class HeaderUtil {

    protected static final int HEADER_LENGTH = 30;
    protected static final int HEADER_ID_LENGTH = 12;
    protected static final byte[] RESERVADO = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    static void serialize(HeaderData h, ByteBuf out) {
        out.writeByte(h.getVersao());
        out.writeByte(h.getFormato());
        out.writeByte(h.getServico());
        out.writeByte(h.getTipo());
        out.writeByte(h.getFormatoId());
        out.writeCharSequence(String.format("%012d", h.getId()), StandardCharsets.ISO_8859_1);
        out.writeBytes(RESERVADO);
    }

    public static HeaderData deserialize(ByteBuf header) {
        byte versao = header.readByte();
        byte formato = header.readByte();
        byte servico = header.readByte();
        byte tipo = header.readByte();
        byte formatoId = header.readByte();

        byte[] idArray = new byte[HEADER_ID_LENGTH];
        header.readBytes(idArray, 0, HEADER_ID_LENGTH);
        String id = new String(idArray, StandardCharsets.ISO_8859_1);

        header.skipBytes(RESERVADO.length);

        HeaderData headerData = new HeaderData();
        headerData.setVersao(versao);
        headerData.setFormato(formato);
        headerData.setServico(servico);
        headerData.setTipo(tipo);
        headerData.setFormatoId(formatoId);
        headerData.setId(Long.parseLong(id));

        return headerData;
    }

}
