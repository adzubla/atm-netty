package com.example.atm.server.impl;

import java.util.Objects;

public class Route {

    private final String queueManager;
    private final String destinationQueue;

    public Route(String queueManager, String destinationQueue) {
        this.queueManager = queueManager;
        this.destinationQueue = destinationQueue;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public String getDestinationQueue() {
        return destinationQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return queueManager.equals(route.queueManager) &&
                destinationQueue.equals(route.destinationQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queueManager, destinationQueue);
    }

}
