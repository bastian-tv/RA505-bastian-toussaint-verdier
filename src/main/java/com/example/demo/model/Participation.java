package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer note;
    @Enumerated(EnumType.STRING)
    private Position position;
    @ManyToOne
    private Fixture fixture;
    @ManyToOne
    private Player player;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public Fixture getFixture() { return fixture; }
    public void setFixture(Fixture fixture) { this.fixture = fixture; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}
