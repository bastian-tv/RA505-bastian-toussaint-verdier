package com.example.demo.exception;

public class FixtureSheetNotFoundException extends RuntimeException {
    public FixtureSheetNotFoundException(Integer id) {
        super("Could not find fixture sheet " + id);
    }
}