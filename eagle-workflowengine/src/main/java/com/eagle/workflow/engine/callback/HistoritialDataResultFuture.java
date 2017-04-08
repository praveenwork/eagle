package com.eagle.workflow.engine.callback;

import org.springframework.util.concurrent.SettableListenableFuture;

public class HistoritialDataResultFuture {

    private final SettableListenableFuture<Object> historitialDataResultFuture = new SettableListenableFuture<>();

    public SettableListenableFuture<Object> getHistoritialDataResultFuture() {
        return historitialDataResultFuture;
    }
}
