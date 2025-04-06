package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Vacunacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Integer> {
}
