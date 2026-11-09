package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Fixture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private String adress;
    private String opposant;
    private Boolean domicile;
    private Integer scoreHome;
    private Integer scoreOutside;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getAdress() { return adress; }
    public void setAdress(String adress) { this.adress = adress; }
    public String getOpposant() { return opposant; }
    public void setOpposant(String opposant) { this.opposant = opposant; }
    public Boolean getDomicile() { return domicile; }
    public void setDomicile(Boolean domicile) { this.domicile = domicile; }
    public Integer getScoreHome() { return scoreHome; }
    public void setScoreHome(Integer scoreHome) { this.scoreHome = scoreHome; }
    public Integer getScoreOutside() { return scoreOutside; }
    public void setScoreOutside(Integer scoreOutside) { this.scoreOutside = scoreOutside; }
}
