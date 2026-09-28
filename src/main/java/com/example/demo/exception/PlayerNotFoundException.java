package com.example.demo.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Integer id) {
        super("Could not find player " + id);
    }
}