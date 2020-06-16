package com.example.atm.netty.codec.atm;

public class AtmMessage {

    private String id;
    private String body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "AtmMessage{" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
