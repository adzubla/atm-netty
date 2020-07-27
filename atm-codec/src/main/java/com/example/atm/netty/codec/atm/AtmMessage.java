package com.example.atm.netty.codec.atm;

public class AtmMessage {

    public static final int ID_LENGTH = 7;

    private String id;
    private final String body;

    public AtmMessage(String id, String body) {
        this.id = id;
        this.body = body;
        checkId();
        checkBody();
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "AtmMessage{" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    private void checkId() {
        if (id == null) {
            throw new IllegalArgumentException("id nao pode ser null");
        }

        if (id.length() < ID_LENGTH) {
            StringBuilder sb = new StringBuilder("0000000");
            id = sb.substring(id.length()) + id;
        }

        if (id.length() > ID_LENGTH) {
            throw new IllegalArgumentException("id nao pode ser maior que " + ID_LENGTH + ": " + id);
        }
    }

    private void checkBody() {
        if (body == null) {
            throw new IllegalArgumentException("body nao pode ser null");
        }
    }

}
