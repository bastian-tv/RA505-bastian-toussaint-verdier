package com.example.demo.controller;

import com.example.demo.exception.PlayerNotFoundException;
import com.example.demo.model.Comment;
import com.example.demo.model.Participation;
import com.example.demo.model.Player;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ParticipationRepository;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping(path="/players")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping(path="")
    public Iterable<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping(path="/{id}")
    public Player getOnePlayer(@PathVariable Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    @GetMapping(path="/{id}/participefixtures")
    public boolean participeFixture(@PathVariable Integer id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
        List<Participation> participations = participationRepository.findByPlayer(player);
        return !participations.isEmpty();
    }

    @GetMapping(path="/{id}/comments")
    public List<Comment> getComments(@PathVariable Integer id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
        return commentRepository.findByPlayer(player);
    }

    @PostMapping(path="")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Integer> addNewPlayer(@RequestBody Player player) {
        if (player.getIdPlayer() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant ne doit pas être fourni lors de la création.");
        }
        validatePlayer(player);
        Player savedPlayer = playerRepository.save(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer.getIdPlayer());
    }

    @PostMapping(path="/{id}/comments")
    public ResponseEntity<Void> addComment(@PathVariable Integer id, @RequestBody Comment commentInput) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));

        if (commentInput.getText() == null || commentInput.getText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing or invalid parameters");
        }

        commentInput.setId(null);
        commentInput.setDate(java.time.LocalDate.now());
        commentInput.setPlayer(player);
        commentRepository.save(commentInput);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<Void> updatePlayer(@PathVariable Integer id, @RequestBody Player newPlayer) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));

        if (newPlayer.getName() != null) player.setName(newPlayer.getName());
        if (newPlayer.getFirstName() != null) player.setFirstName(newPlayer.getFirstName());
        if (newPlayer.getNumLicense() != null) player.setNumLicense(newPlayer.getNumLicense());
        if (newPlayer.getDateOfBirth() != null) player.setDateOfBirth(newPlayer.getDateOfBirth());
        if (newPlayer.getSize() != null) player.setSize(newPlayer.getSize());
        if (newPlayer.getWeight() != null) player.setWeight(newPlayer.getWeight());
        if (newPlayer.getStatus() != null) player.setStatus(newPlayer.getStatus());

        validatePlayer(player);
        playerRepository.save(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> deletePlayer(@PathVariable Integer id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));

        List<Participation> participations = participationRepository.findByPlayer(player);
        if (!participations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player participates in match.");
        }

        commentRepository.deleteAll(commentRepository.findByPlayer(player));
        playerRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    private void validatePlayer(Player player) {
        if (player.getName() == null || player.getName().isBlank()
                || player.getFirstName() == null || player.getFirstName().isBlank()
                || player.getNumLicense() == null || player.getNumLicense() <= 0
                || player.getDateOfBirth() == null || player.getDateOfBirth().isAfter(java.time.LocalDate.now())
                || player.getSize() == null || player.getSize() <= 0
                || player.getWeight() == null || !Float.isFinite(player.getWeight()) || player.getWeight() <= 0
                || player.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing or invalid player parameters");
        }
    }
}
