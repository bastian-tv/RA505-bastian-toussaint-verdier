package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.ArrayList;

@Entity
public class FixtureSheet {
    @Id
    private Long id;
    private Integer idFixture;
    private ArrayList<Participation> participations;

    public FixtureSheet() {}

    public Integer getIdFixture() { return idFixture; }

    public ArrayList<Participation> getParticipations() { return participations; }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }
}
