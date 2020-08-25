package com.example.atm.server.jms;

import org.springframework.stereotype.Component;

import javax.jms.TemporaryQueue;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReplyToHolder {

    private final Map<String, TemporaryQueue> replyToQueue = new HashMap<>();

    public TemporaryQueue getReplyToQueue(String id) {
        return replyToQueue.get(id);
    }

    public void setReplyToQueue(String id, TemporaryQueue replyToQueue) {
        this.replyToQueue.put(id, replyToQueue);
    }

}
