package com.hiketrackbackend.hiketrackbackend.exception;

public class MemoryLimitException extends RuntimeException {
    public MemoryLimitException(String message) {
        super(message);
    }
}
