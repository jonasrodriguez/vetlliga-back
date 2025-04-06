package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Peso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PesoRepository extends JpaRepository<Peso, Integer> {

}
