package com.miilhozinho.chunkgenerator.util;

import com.miilhozinho.chunkgenerator.events.ProgressUpdateEvent;

@FunctionalInterface
public interface ProgressEventListener {
    void onProgressUpdate(ProgressUpdateEvent event);
}
