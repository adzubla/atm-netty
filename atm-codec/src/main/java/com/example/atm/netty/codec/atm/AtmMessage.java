package com.example.atm.netty.codec.atm;

public class AtmMessage {

    private final String body;
    private final Long id;

    public AtmMessage(Long id, String body) {
        this.id = id;
        this.body = body;
    }

    public Long getId() {
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

}
