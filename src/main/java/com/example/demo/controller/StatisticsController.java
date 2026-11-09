package com.example.demo.controller;

import com.example.demo.exception.PlayerNotFoundException;
import com.example.demo.model.Fixture;
import com.example.demo.model.Participation;
import com.example.demo.repository.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/statistiques")
public class StatisticsController {
    private final FixtureRepository fixtures;
    private final PlayerRepository players;
    private final ParticipationRepository participations;

    public StatisticsController(FixtureRepository fixtures, PlayerRepository players, ParticipationRepository participations) {
        this.fixtures = fixtures;
        this.players = players;
        this.participations = participations;
    }

    private List<Fixture> completed() {
        return StreamSupport.stream(fixtures.findAll().spliterator(), false)
                .filter(f -> f.getDate() != null && !f.getDate().isAfter(LocalDate.now())
                        && f.getScoreHome() != null && f.getScoreOutside() != null)
                .sorted(Comparator.comparing(Fixture::getDate).thenComparing(Fixture::getId).reversed()).toList();
    }

    private boolean won(Fixture f) {
        return Boolean.TRUE.equals(f.getDomicile()) ? f.getScoreHome() > f.getScoreOutside() : f.getScoreOutside() > f.getScoreHome();
    }

    private double percent(long value, long total) {
        return total == 0 ? 0 : Math.round(value * 1000.0 / total) / 10.0;
    }

    @GetMapping("/fixtures")
    public Map<String, Object> fixtures() {
        var completed = completed();
        long won = completed.stream().filter(this::won).count();
        long draw = completed.stream().filter(f -> f.getScoreHome().equals(f.getScoreOutside())).count();
        long lost = completed.size() - won - draw;
        return Map.of("total", completed.size(), "won", won, "draw", draw, "lost", lost,
                "percentWon", percent(won, completed.size()), "percentDraw", percent(draw, completed.size()),
                "percentLost", percent(lost, completed.size()));
    }

    @GetMapping("/players/{id}")
    public Map<String, Object> player(@PathVariable Integer id) {
        var player = players.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
        var completed = completed();
        var completedIds = completed.stream().map(Fixture::getId).collect(Collectors.toSet());
        var selected = participations.findByPlayer(player).stream()
                .filter(p -> completedIds.contains(p.getFixture().getId())).toList();
        var selectedIds = selected.stream().map(p -> p.getFixture().getId()).collect(Collectors.toSet());
        int consecutive = 0;
        for (var fixture : completed) {
            if (!selectedIds.contains(fixture.getId())) break;
            consecutive++;
        }
        var preferred = selected.stream().filter(p -> p.getPosition().getJerseyNumber() <= 15)
                .collect(Collectors.groupingBy(Participation::getPosition, Collectors.counting()))
                .entrySet().stream().sorted(Comparator.<Map.Entry<com.example.demo.model.Position, Long>>comparingLong(Map.Entry::getValue)
                        .reversed().thenComparingInt(e -> e.getKey().getJerseyNumber()))
                .map(e -> e.getKey().getLabel()).findFirst().orElse(null);
        var average = selected.stream().map(Participation::getNote).filter(Objects::nonNull).mapToInt(Integer::intValue).average();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("statut", player.getStatus());
        result.put("poste_prefere", preferred);
        result.put("titularisations", selected.stream().filter(p -> p.getPosition().getJerseyNumber() <= 15).count());
        result.put("remplacements", selected.stream().filter(p -> p.getPosition().getJerseyNumber() > 15).count());
        result.put("moyenne_evaluations", average.isPresent() ? average.getAsDouble() : null);
        result.put("pourcentage_victoires", percent(completed.stream().filter(f -> selectedIds.contains(f.getId())).filter(this::won).count(), selectedIds.size()));
        result.put("selections_consecutives", consecutive);
        result.put("total_participations", selected.size());
        return result;
    }
}
