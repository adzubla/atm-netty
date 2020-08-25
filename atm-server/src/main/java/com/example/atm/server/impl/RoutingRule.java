package com.example.atm.server.impl;

import java.util.regex.Pattern;

public class RoutingRule {

    private final Pattern atmIdPattern;
    private final int lineNumber;
    private final String atmId;
    private final String msgId;
    private final String destinationQueue;
    private final String queueManager;

    public RoutingRule(int lineNumber, String atmId, String msgId, String destinationQueue, String queueManager) {
        this.lineNumber = lineNumber;
        this.atmId = atmId;
        this.msgId = msgId;
        this.destinationQueue = destinationQueue;
        this.queueManager = queueManager;
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

    public String getQueueManager() {
        return queueManager;
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
                ", queueManager='" + queueManager + '\'' +
                '}';
    }

}
