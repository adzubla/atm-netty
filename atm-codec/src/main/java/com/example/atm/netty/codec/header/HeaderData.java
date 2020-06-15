package com.example.atm.netty.codec.header;

import io.netty.util.AttributeKey;

public class HeaderData {

    public static final AttributeKey<HeaderData> HEADER_DATA_ATTRIBUTE_KEY = AttributeKey.newInstance("HeaderData.attr");

    public static int ID_LENGTH = 12;

    private byte versao = 1;
    private byte formato = 1;
    private byte servico = 5;
    private byte tipo = 3;
    private byte formatoId = 1;
    private byte[] reservado = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private String id;

    public HeaderData(String id) {
        this.id = id;
        checkId();
    }

    public HeaderData(String localidade, String idTerminal) {
        this(buildId(localidade, idTerminal));
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
        if (reservado == null || reservado.length != 13) {
            throw new IllegalArgumentException("Reservado invalido");
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

    public String getLocalidade() {
        return id.substring(5, 5 + 3);
    }

    public String getIdTerminal() {
        return id.substring(8, 8 + 4);
    }

    private void checkId() {
        if (id == null) {
            throw new IllegalArgumentException("Id nao pode ser null");
        }

        if (id.length() < ID_LENGTH) {
            StringBuilder sb = new StringBuilder("000000000000");
            id = sb.substring(id.length()) + id;
        }

        if (id.length() > ID_LENGTH) {
            throw new IllegalArgumentException("Id nao pode ser maior que " + ID_LENGTH);
        }
    }

    private static String buildId(String localidade, String idTerminal) {
        if (localidade == null || localidade.length() != 3) {
            throw new IllegalArgumentException("Localidade invalida");
        }
        if (idTerminal == null || idTerminal.length() != 4) {
            throw new IllegalArgumentException("IdTerminal invalida");
        }
        return localidade + idTerminal;
    }

}
