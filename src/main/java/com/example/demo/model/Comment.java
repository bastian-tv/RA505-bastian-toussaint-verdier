package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Comment {

    @Id
    private Long id;
    private String text;
    private LocalDate date;
    @ManyToOne
    private Player player;

    public Player getPlayer() { return player; }

    public void set$player(Player $player) { this.player = $player; }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }
}
