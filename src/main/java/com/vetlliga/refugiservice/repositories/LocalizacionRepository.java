package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Localizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizacionRepository extends JpaRepository<Localizacion, Integer>, JpaSpecificationExecutor<Animal> {

}
