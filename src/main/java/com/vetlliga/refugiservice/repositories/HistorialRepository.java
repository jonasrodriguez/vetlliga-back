package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {

}
