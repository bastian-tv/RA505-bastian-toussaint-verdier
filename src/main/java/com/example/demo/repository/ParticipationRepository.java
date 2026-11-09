package com.example.demo.repository;

import com.example.demo.model.Participation;
import com.example.demo.model.Player;
import com.example.demo.model.Fixture;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ParticipationRepository extends CrudRepository<Participation, Integer> {
    List<Participation> findByPlayer(Player player);
    List<Participation> findByFixture(Fixture fixture);
}
