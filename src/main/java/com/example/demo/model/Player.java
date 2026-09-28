package com.example.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private @Nullable Integer idPlayer;
    private int numLicense;
    private String name;
    private String firstName;
    private LocalDate dateOfBirth;
    private int size;
    private float weight;
    private Status status;

    public Player() {}

    public @org.jspecify.annotations.Nullable Integer getIdPlayer() {
        return idPlayer;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getNumLicense() {
        return numLicense;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public int getSize() {
        return size;
    }

    public float getWeight() {
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

    public void setNumLicense(int numLicense) {
        this.numLicense = numLicense;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
