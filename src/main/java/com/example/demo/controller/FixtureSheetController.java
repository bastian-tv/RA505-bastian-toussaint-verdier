package com.example.demo.controller;

import com.example.demo.exception.FixtureSheetNotFoundException;

import com.example.demo.model.FixtureSheet;
import com.example.demo.repository.FixtureSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(path="/fixtureSheet")
public class FixtureSheetController {
    @Autowired
    private FixtureSheetRepository fixtureSheetRepository;

    @Autowired
    private com.example.demo.repository.FixtureRepository fixtureRepository;

    @Autowired
    private com.example.demo.repository.ParticipationRepository participationRepository;

    @PostMapping(path={"", "/"})
    public ResponseEntity<FixtureSheet> addNewFixtureSheet(@RequestBody FixtureSheet fixtureSheet) {
        if (fixtureSheet.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant ne doit pas être fourni lors de la création.");
        }
        validateSheet(fixtureSheet);
        FixtureSheet savedFixtureSheet = fixtureSheetRepository.save(fixtureSheet);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFixtureSheet);
    }

    @GetMapping(path="/all")
    public Iterable<FixtureSheet> getAllFixtureSheet() {
        return fixtureSheetRepository.findAll();
    }


    @GetMapping(path="/{id}")
    public FixtureSheet getOneFixtureSheet(@PathVariable Integer id) {
        return fixtureSheetRepository.findById(id)
                .orElseThrow(() -> new FixtureSheetNotFoundException(id));
    }

    @PutMapping("/{id}")
    public FixtureSheet replaceFixtureSheet(@RequestBody FixtureSheet newFixtureSheet, @PathVariable Integer id) {

        return fixtureSheetRepository.findById(id)
                .map(fixtureSheet -> {
                    fixtureSheet.setIdFixture(newFixtureSheet.getIdFixture());
                    fixtureSheet.setParticipations(newFixtureSheet.getParticipations());
                    return fixtureSheetRepository.save(fixtureSheet);
                })
                .orElseThrow(() -> new FixtureSheetNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    public void deleteFixtureSheet(@PathVariable Integer id) {
        fixtureSheetRepository.deleteById(id);
    }
    private void validateSheet(FixtureSheet sheet) {
        if (sheet.getIdFixture() == null || sheet.getParticipations() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "A fixture and a participation list are required");
        }
        fixtureRepository.findById(sheet.getIdFixture())
                .orElseThrow(() -> new com.example.demo.exception.FixtureNotFoundException(sheet.getIdFixture()));
        var resolved = new java.util.ArrayList<com.example.demo.model.Participation>();
        var ids = new java.util.HashSet<Integer>();
        for (var input : sheet.getParticipations()) {
            if (input == null || input.getId() == null || !ids.add(input.getId())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid or duplicate participation");
            }
            var participation = participationRepository.findById(input.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found"));
            if (!participation.getFixture().getId().equals(sheet.getIdFixture())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Participation belongs to another fixture");
            }
            resolved.add(participation);
        }
        sheet.setParticipations(resolved);
    }
}
