package com.example.footballbooking.repository;

import com.example.footballbooking.model.FootballField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField,Long> {
}
