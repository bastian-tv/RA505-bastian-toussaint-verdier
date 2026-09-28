package com.example.demo.controller;

import com.example.demo.exception.PlayerNotFoundException;
import com.example.demo.model.Player;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RestController
@RequestMapping(path="/players")
public class PlayerController {
    @Autowired
    private PlayerRepository playerRepository;

    @PostMapping(path="/")
    public ResponseEntity<Player> addNewPlayer(@RequestBody Player player) {
        if (player.getIdPlayer() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant ne doit pas être fourni lors de la création.");
        }
        Player savedPlayer = playerRepository.save(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
    }

    @GetMapping(path="/all")
    public Iterable<Player> getAllPlayer() {
        return playerRepository.findAll();
    }


    @GetMapping(path="/{id}")
    public Player getOnePlayer(@PathVariable Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    @PutMapping("/{id}")
    public Player replaceEmployee(@RequestBody Player newPlayer, @PathVariable Integer id) {

        return playerRepository.findById(id)
                .map(player -> {
                    player.setName(newPlayer.getName());
                    player.setName(newPlayer.getName());
                    player.setFirstName(newPlayer.getFirstName());
                    player.setNumLicense(newPlayer.getNumLicense());
                    player.setDateOfBirth(newPlayer.getDateOfBirth());
                    player.setSize(newPlayer.getSize());
                    player.setWeight(newPlayer.getWeight());
                    player.setStatus(newPlayer.getStatus());
                    return playerRepository.save(player);
                })
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Integer id) {
        playerRepository.deleteById(id);
    }
}
