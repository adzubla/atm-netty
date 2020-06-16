package com.example.atm.netty.codec.header;

import io.netty.buffer.ByteBuf;

import static com.example.atm.netty.codec.atm.AtmMessage.ID_LENGTH;
import static com.example.atm.netty.codec.header.HeaderData.RESERVED_LENGTH;

public class HeaderUtil {

    static void serialize(HeaderData h, ByteBuf out) {
        out.writeByte(h.getVersao());
        out.writeByte(h.getFormato());
        out.writeByte(h.getServico());
        out.writeByte(h.getTipo());
        out.writeByte(h.getFormatoId());
        out.writeBytes(h.getId().getBytes());
        out.writeBytes(h.getReservado());
    }

    public static HeaderData deserialize(ByteBuf header) {
        byte versao = header.readByte();
        byte formato = header.readByte();
        byte servico = header.readByte();
        byte tipo = header.readByte();
        byte formatoId = header.readByte();

        byte[] id = new byte[ID_LENGTH];
        header.readBytes(id, 0, ID_LENGTH);

        byte[] reservado = new byte[RESERVED_LENGTH];
        header.readBytes(reservado, 0, RESERVED_LENGTH);

        HeaderData headerData = new HeaderData(new String(id));
        headerData.setVersao(versao);
        headerData.setFormato(formato);
        headerData.setServico(servico);
        headerData.setTipo(tipo);
        headerData.setFormatoId(formatoId);
        headerData.setReservado(reservado);

        return headerData;
    }

}
