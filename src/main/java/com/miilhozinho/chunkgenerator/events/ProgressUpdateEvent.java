package com.miilhozinho.chunkgenerator.events;


import java.util.Objects;

public class ProgressUpdateEvent {
    private final double percentage;
    private final long currentChunks;
    private final long totalChunks;

    public ProgressUpdateEvent(double percentage, long currentChunks, long totalChunks) {
        this.percentage = percentage;
        this.currentChunks = currentChunks;
        this.totalChunks = totalChunks;
    }

    public double percentage() {
        return percentage;
    }

    public long currentChunks() {
        return currentChunks;
    }

    public long totalChunks() {
        return totalChunks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ProgressUpdateEvent) obj;
        return Double.doubleToLongBits(this.percentage) == Double.doubleToLongBits(that.percentage) &&
                this.currentChunks == that.currentChunks &&
                this.totalChunks == that.totalChunks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage, currentChunks, totalChunks);
    }

    @Override
    public String toString() {
        return "ProgressUpdateEvent[" +
                "percentage=" + percentage + ", " +
                "currentChunks=" + currentChunks + ", " +
                "totalChunks=" + totalChunks + ']';
    }

}