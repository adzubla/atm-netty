package com.example.atm.netty.codec.header;

import io.netty.util.AttributeKey;

import java.util.Arrays;

public class HeaderData {

    public static final AttributeKey<HeaderData> HEADER_DATA_ATTRIBUTE_KEY = AttributeKey.newInstance("HeaderData.attr");

    public static final int HEADER_LENGTH = 30;
    public static final int ID_LENGTH = 12;
    public static final int RESERVED_LENGTH = 13;

    private byte versao = 1;
    private byte formato = 1;
    private byte servico = 5;
    private byte tipo = 3;
    private byte formatoId = 1;
    private byte[] reservado = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private final String id;

    public HeaderData(String id) {
        if (id == null || id.length() != ID_LENGTH) {
            throw new IllegalArgumentException("id invalido: " + id);
        }
        this.id = id;
    }

    public byte getVersao() {
        return versao;
    }

    public void setVersao(byte versao) {
        this.versao = versao;
    }

    public byte getFormato() {
        return formato;
    }

    public void setFormato(byte formato) {
        this.formato = formato;
    }

    public byte getServico() {
        return servico;
    }

    public void setServico(byte servico) {
        this.servico = servico;
    }

    public byte getTipo() {
        return tipo;
    }

    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    public byte getFormatoId() {
        return formatoId;
    }

    public void setFormatoId(byte formatoId) {
        this.formatoId = formatoId;
    }

    public byte[] getReservado() {
        return reservado;
    }

    public void setReservado(byte[] reservado) {
        if (reservado == null || reservado.length != RESERVED_LENGTH) {
            throw new IllegalArgumentException("reservado invalido: " + Arrays.toString(reservado));
        }
        this.reservado = reservado;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "HeaderData{" +
                "id='" + id + '\'' +
                '}';
    }

}
