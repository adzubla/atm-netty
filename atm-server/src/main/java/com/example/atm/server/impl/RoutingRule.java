package com.example.atm.server.impl;

import java.util.regex.Pattern;

public class RoutingRule {

    private final Pattern atmIdPattern;
    private final int lineNumber;
    private final String atmId;
    private final String msgId;
    private final String destinationQueue;

    public RoutingRule(int lineNumber, String atmId, String msgId, String destinationQueue) {
        this.lineNumber = lineNumber;
        this.atmId = atmId;
        this.msgId = msgId;
        this.destinationQueue = destinationQueue;
        if (!atmId.equals("*")) {
            this.atmIdPattern = Pattern.compile(atmId);
        } else {
            this.atmIdPattern = null;
        }
    }

    public String getAtmId() {
        return atmId;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getDestinationQueue() {
        return destinationQueue;
    }

    public Pattern getAtmIdPattern() {
        return atmIdPattern;
    }

    @Override
    public String toString() {
        return "RoutingRule{" +
                "lineNumber=" + lineNumber +
                ", atmId='" + atmId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", destinationQueue='" + destinationQueue + '\'' +
                '}';
    }

}
