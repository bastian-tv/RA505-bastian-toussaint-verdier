package com.example.demo.controller;

public record ScoreDto(Integer home, Integer outside) {
    public Integer getHome() { return home; }
    public Integer getOutside() { return outside; }
}
