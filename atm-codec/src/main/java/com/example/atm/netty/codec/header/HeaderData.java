package com.example.atm.netty.codec.header;

import io.netty.util.AttributeKey;

public class HeaderData {

    public static final AttributeKey<HeaderData> HEADER_DATA_ATTRIBUTE_KEY = AttributeKey.newInstance("HeaderData.attr");

    public static final int HEADER_LENGTH = 30;
    public static final int HEADER_ID_LENGTH = 12;

    public static final byte PING = 1;
    public static final byte PONG = 2;
    public static final byte DATA = 3;

    private byte versao = 1;
    private byte formato = 1;
    private byte servico = 5;
    private byte tipo = 3;
    private byte formatoId = 1;
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.length() != HEADER_ID_LENGTH) {
            throw new IllegalArgumentException("id invalido: " + id);
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return "HeaderData{" +
                "id='" + id + '\'' +
                '}';
    }

}
