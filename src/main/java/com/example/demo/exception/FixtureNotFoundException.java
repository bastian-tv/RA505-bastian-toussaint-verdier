package com.example.demo.exception;

public class FixtureNotFoundException extends RuntimeException {
    public FixtureNotFoundException(Integer id) {
        super("Could not find fixture " + id);
    }
}