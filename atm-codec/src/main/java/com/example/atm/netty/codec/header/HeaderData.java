package com.example.atm.netty.codec.header;

import io.netty.util.AttributeKey;

public class HeaderData {

    public static final AttributeKey<Long> HEADER_ID_ATTRIBUTE_KEY = AttributeKey.newInstance("HeaderData.id");
    public static final AttributeKey<Byte> HEADER_TYPE_ATTRIBUTE_KEY = AttributeKey.newInstance("HeaderData.type");

    public static final byte PING = 1;
    public static final byte PONG = 2;
    public static final byte DATA = 3;

    private byte versao = 1;
    private byte formato = 1;
    private byte servico = 5;
    private byte tipo = DATA;
    private byte formatoId = 1;
    private Long id;

    public HeaderData() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null || id > 999999999999L) {
            throw new IllegalArgumentException("Header id invalido: " + id);
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
