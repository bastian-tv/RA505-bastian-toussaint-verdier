package com.example.demo.controller;

import com.example.demo.exception.FixtureNotFoundException;
import com.example.demo.model.Fixture;
import com.example.demo.model.Participation;
import com.example.demo.model.Position;
import com.example.demo.repository.FixtureRepository;
import com.example.demo.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping(path="/fixtures")
public class FixtureController {

    @Autowired
    private FixtureRepository fixtureRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private com.example.demo.repository.FixtureSheetRepository fixtureSheetRepository;

    @GetMapping(path="")
    public Iterable<Fixture> getAllFixtures() {
        return fixtureRepository.findAll();
    }

    @GetMapping(path="/{id}")
    public Fixture getOneFixture(@PathVariable Integer id) {
        return fixtureRepository.findById(id)
                .orElseThrow(() -> new FixtureNotFoundException(id));
    }

    @GetMapping(path="/{id}/complete")
    public String fixtureIsComplete(@PathVariable Integer id) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new FixtureNotFoundException(id));
        List<Participation> participations = participationRepository.findByFixture(fixture);
        for (Participation p : participations) {
            if (p.getPlayer() == null) {
                return "false";
            }
        }
        return participations.size() == Position.values().length ? "true" : "false";
    }

    @PostMapping(path="")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Integer> addNewFixture(@RequestBody Fixture fixture) {
        if (fixture.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant ne doit pas être fourni lors de la création.");
        }
        validateFixture(fixture);
        if (fixture.getScoreHome() != null || fixture.getScoreOutside() != null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Use the score endpoint after the fixture");
        }
        Fixture savedFixture = fixtureRepository.save(fixture);

        for (Position position : Position.values()) {
            Participation participation = new Participation();
            participation.setPosition(position);
            participation.setFixture(savedFixture);
            participationRepository.save(participation);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedFixture.getId());
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<Integer> updateFixture(@PathVariable Integer id, @RequestBody Fixture newFixture) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new FixtureNotFoundException(id));

        if (isPastFixture(fixture)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The fixture is over.");
        }

        if (newFixture.getDate() != null && newFixture.getDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The fixture date is already past.");
        }

        if (newFixture.getDate() != null) {
            fixture.setDate(newFixture.getDate());
        }
        if (newFixture.getOpposant() != null) {
            fixture.setOpposant(newFixture.getOpposant());
        }
        if (newFixture.getAdress() != null) {
            fixture.setAdress(newFixture.getAdress());
        }
        if (newFixture.getDomicile() != null) {
            fixture.setDomicile(newFixture.getDomicile());
        }
        if (newFixture.getTime() != null) {
            fixture.setTime(newFixture.getTime());
        }

        fixtureRepository.save(fixture);
        return ResponseEntity.status(HttpStatus.OK).body(fixture.getId());
    }

    @PatchMapping(path="/{id}/score")
    public ResponseEntity<Void> updateScore(@PathVariable Integer id, @RequestBody ScoreDto scoreDto) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new FixtureNotFoundException(id));

        if (!isPastFixture(fixture)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The fixture is not over.");
        }

        if (scoreDto.home() == null || scoreDto.outside() == null || scoreDto.home() < 0 || scoreDto.outside() < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Scores must be non-negative integers");
        }
        fixture.setScoreHome(scoreDto.getHome());
        fixture.setScoreOutside(scoreDto.getOutside());
        fixtureRepository.save(fixture);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> deleteFixture(@PathVariable Integer id) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new FixtureNotFoundException(id));

        if (isPastFixture(fixture)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The fixture is over.");
        }

        fixtureSheetRepository.deleteAll(fixtureSheetRepository.findByIdFixture(id));
        participationRepository.deleteAll(participationRepository.findByFixture(fixture));
        fixtureRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private void validateFixture(Fixture fixture) {
        if (fixture.getDate() == null || fixture.getOpposant() == null || fixture.getOpposant().isBlank()
                || fixture.getAdress() == null || fixture.getAdress().isBlank() || fixture.getDomicile() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing or invalid fixture parameters");
        }
    }

    private boolean isPastFixture(Fixture fixture) {
        if (fixture.getDate() == null) {
            return false;
        }
        LocalDateTime matchDateTime = fixture.getDate().atTime(fixture.getTime() != null ? fixture.getTime() : LocalTime.MIDNIGHT);
        return matchDateTime.isBefore(LocalDateTime.now());
    }
}