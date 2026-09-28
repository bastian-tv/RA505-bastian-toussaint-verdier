package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Participation {
    @Id
    private Long id;

    private Integer note;
    private Position namePosition;
    @ManyToOne
    private Fixture $fixture;
    @ManyToOne
    private Player $player;

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }
}
