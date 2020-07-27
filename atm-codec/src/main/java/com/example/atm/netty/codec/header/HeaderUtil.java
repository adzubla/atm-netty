package com.example.atm.netty.codec.header;

import io.netty.buffer.ByteBuf;

import static com.example.atm.netty.codec.header.HeaderData.HEADER_ID_LENGTH;

public class HeaderUtil {

    private static final byte[] RESERVADO = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    static void serialize(HeaderData h, ByteBuf out) {
        out.writeByte(h.getVersao());
        out.writeByte(h.getFormato());
        out.writeByte(h.getServico());
        out.writeByte(h.getTipo());
        out.writeByte(h.getFormatoId());
        out.writeBytes(h.getId().getBytes());
        out.writeBytes(RESERVADO);
    }

    public static HeaderData deserialize(ByteBuf header) {
        byte versao = header.readByte();
        byte formato = header.readByte();
        byte servico = header.readByte();
        byte tipo = header.readByte();
        byte formatoId = header.readByte();

        byte[] id = new byte[HEADER_ID_LENGTH];
        header.readBytes(id, 0, HEADER_ID_LENGTH);

        header.skipBytes(RESERVADO.length);

        HeaderData headerData = new HeaderData();
        headerData.setVersao(versao);
        headerData.setFormato(formato);
        headerData.setServico(servico);
        headerData.setTipo(tipo);
        headerData.setFormatoId(formatoId);
        headerData.setId(new String(id));

        return headerData;
    }

}
