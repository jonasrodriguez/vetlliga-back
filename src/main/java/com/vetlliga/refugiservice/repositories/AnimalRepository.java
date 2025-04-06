package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {

}
