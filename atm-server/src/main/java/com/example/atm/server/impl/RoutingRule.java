package com.example.atm.server.impl;

public class RoutingRule {

    private String atmId;
    private String msgId;
    private String destinationQueue;

    public RoutingRule(String atmId, String msgId, String destinationQueue) {
        this.atmId = atmId;
        this.msgId = msgId;
        this.destinationQueue = destinationQueue;
    }

    public String getAtmId() {
        return atmId;
    }

    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getDestinationQueue() {
        return destinationQueue;
    }

    public void setDestinationQueue(String destinationQueue) {
        this.destinationQueue = destinationQueue;
    }

    @Override
    public String toString() {
        return "RoutingRule{" +
                "atmId='" + atmId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", destinationQueue='" + destinationQueue + '\'' +
                '}';
    }
}
