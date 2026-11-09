package com.example.demo.repository;

import com.example.demo.model.FixtureSheet;
import org.springframework.data.repository.CrudRepository;

public interface FixtureSheetRepository extends CrudRepository<FixtureSheet, Integer> {
    java.util.List<FixtureSheet> findByIdFixture(Integer idFixture);
}