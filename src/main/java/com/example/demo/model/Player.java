package com.example.demo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer idPlayer;
    private Integer numLicense;
    private String name;
    private String firstName;
    private LocalDate dateOfBirth;
    private Integer size;
    private Float weight;
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private Status status;

    public Player() {}

    public Integer getIdPlayer() {
        return idPlayer;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getNumLicense() {
        return numLicense;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getSize() {
        return size;
    }

    public Float getWeight() {
        return weight;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setNumLicense(Integer numLicense) {
        this.numLicense = numLicense;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
