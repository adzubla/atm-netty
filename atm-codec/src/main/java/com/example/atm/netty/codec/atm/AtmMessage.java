package com.example.atm.netty.codec.atm;

public class AtmMessage {

    private final byte[] body;
    private final Long id;

    public AtmMessage(Long id, byte[] body) {
        this.id = id;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "AtmMessage{" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}
