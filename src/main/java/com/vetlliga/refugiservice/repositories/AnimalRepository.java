package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.entities.Animal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer>, JpaSpecificationExecutor<Animal> {

  @Query("""
    SELECT a.id, a.tipo, a.nombre, p.peso, MAX(d.fecha)
    FROM Animal a
    LEFT JOIN a.desparasitaciones d ON d.tipo = :tipo
    LEFT JOIN a.pesos p ON p.fecha = (SELECT MAX(p2.fecha) FROM Peso p2 WHERE p2.animal = a)
    WHERE a.activo = true
    AND a.estado = 'EN_PROTECTORA'
    GROUP BY a.id, a.tipo, a.nombre, p.peso
    HAVING MAX(d.fecha) IS NULL
        OR MAX(d.fecha) < CASE WHEN a.tipo = 'PERRO' THEN :limitePerro ELSE :limiteGato END
""")
  List<Object[]> findPendientesDesparasitacion(
      @Param("tipo") TipoDesparasitacion tipo,
      @Param("limitePerro") LocalDateTime limitePerro,
      @Param("limiteGato") LocalDateTime limiteGato);

  boolean existsByLocalizacionId(Integer localizacionId);
}
