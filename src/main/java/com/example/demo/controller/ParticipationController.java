package com.example.demo.controller;

import com.example.demo.exception.FixtureNotFoundException;
import com.example.demo.exception.PlayerNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    private final ParticipationRepository participations;
    private final FixtureRepository fixtures;
    private final PlayerRepository players;

    public ParticipationController(ParticipationRepository participations, FixtureRepository fixtures, PlayerRepository players) {
        this.participations = participations;
        this.fixtures = fixtures;
        this.players = players;
    }

    @GetMapping("/{id}")
    public Participation get(@PathVariable Integer id) {
        return participations.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found"));
    }

    @GetMapping("/fixtures/{id}")
    public List<Participation> byFixture(@PathVariable Integer id) {
        return participations.findByFixture(fixtures.findById(id).orElseThrow(() -> new FixtureNotFoundException(id)));
    }

    public record PlayerInput(Integer playerId) {}
    public record NoteInput(Integer note) {}

    @PatchMapping("/{id}/player")
    @Transactional
    public void assign(@PathVariable Integer id, @RequestBody PlayerInput input) {
        var participation = get(id);
        requireFuture(participation);
        if (input.playerId() == null || input.playerId() <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid playerId");
        }
        var player = players.findById(input.playerId()).orElseThrow(() -> new PlayerNotFoundException(input.playerId()));
        if (player.getStatus() != Status.Available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player is not available");
        }
        boolean alreadySelected = participations.findByFixture(participation.getFixture()).stream()
                .anyMatch(other -> !other.getId().equals(id) && other.getPlayer() != null
                        && other.getPlayer().getIdPlayer().equals(player.getIdPlayer()));
        if (alreadySelected) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player already selected for this fixture");
        }
        participation.setPlayer(player);
        participations.save(participation);
    }

    @DeleteMapping("/{id}/player")
    @Transactional
    public void unassign(@PathVariable Integer id) {
        var participation = get(id);
        requireFuture(participation);
        participation.setPlayer(null);
        participation.setNote(null);
        participations.save(participation);
    }

    @PatchMapping("/{id}/note")
    @Transactional
    public void note(@PathVariable Integer id, @RequestBody NoteInput input) {
        var participation = get(id);
        if (!isPast(participation.getFixture()) || participation.getPlayer() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A past fixture and an assigned player are required");
        }
        if (input.note() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "An integer note is required");
        }
        participation.setNote(input.note());
        participations.save(participation);
    }

    private void requireFuture(Participation participation) {
        if (isPast(participation.getFixture())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The fixture is over");
        }
    }

    private boolean isPast(Fixture fixture) {
        return fixture.getDate() != null && fixture.getDate().atTime(
                fixture.getTime() == null ? LocalTime.MIDNIGHT : fixture.getTime()).isBefore(LocalDateTime.now());
    }
}
