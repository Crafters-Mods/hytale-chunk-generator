package com.miilhozinho.chunkgenerator.events;


public record ProgressUpdateEvent(double percentage, long currentChunks, long totalChunks) {

    @Override
    public String toString() {
        return "ProgressUpdateEvent[" +
                "percentage=" + percentage + ", " +
                "currentChunks=" + currentChunks + ", " +
                "totalChunks=" + totalChunks + ']';
    }

}