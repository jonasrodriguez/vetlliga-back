package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Desparasitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesparasitacionRepository extends JpaRepository<Desparasitacion, Integer> {

}
