package com.example.demo.repository;
import com.example.demo.model.Fixture;
import org.springframework.data.repository.CrudRepository;

public interface FixtureRepository extends CrudRepository<Fixture, Integer> {
}