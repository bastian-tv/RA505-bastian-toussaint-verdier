package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Fixture {

    @Id
    private Long id;
    private LocalDate date;
    private int time;
    private String adress;
    private String opposant;
    private boolean domicile;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }

    public int getTime() { return time; }

    public String getAdress() { return adress; }

    public String getOpposant() { return opposant; }

    public boolean isDomicile() { return domicile; }

    public void setDate(LocalDate date) { this.date = date; }

    public void setTime(int time) { this.time = time; }

    public void setAdress(String adress) { this.adress = adress; }

    public void setOpposant(String opposant) { this.opposant = opposant; }

    public void setDomicile(boolean domicile) { this.domicile = domicile; }
}
