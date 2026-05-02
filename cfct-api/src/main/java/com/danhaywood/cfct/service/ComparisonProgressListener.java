package com.danhaywood.cfct.service;

@FunctionalInterface
public interface ComparisonProgressListener {

    ComparisonProgressListener NO_OP = event -> {
    };

    void onProgress(ComparisonProgressEvent event);
}
