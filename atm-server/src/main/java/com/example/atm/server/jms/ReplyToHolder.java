package com.example.atm.server.jms;

import org.springframework.stereotype.Component;

import javax.jms.TemporaryQueue;

@Component
public class ReplyToHolder {

    private TemporaryQueue replyToQueue;

    public TemporaryQueue getReplyToQueue() {
        return replyToQueue;
    }

    public void setReplyToQueue(TemporaryQueue replyToQueue) {
        this.replyToQueue = replyToQueue;
    }

}
