package com.vetlliga.refugiservice.repositories;

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
      SELECT a.id, a.tipo, a.nombre, a.numeroRegistro, MAX(d.fecha)
      FROM Animal a
      LEFT JOIN a.desparasitaciones d ON d.tipo = :tipo
      WHERE a.activo = true
      AND a.estado = 'EN_PROTECTORA'
      GROUP BY a.id, a.tipo, a.nombre, a.numeroRegistro
      HAVING MAX(d.fecha) IS NULL OR MAX(d.fecha) < :fechaLimite
    """)
  List<Object[]> findPendientesDesparasitacion(@Param("tipo") TipoDesparasitacion tipo, @Param("fechaLimite") LocalDateTime fechaLimite);

  boolean existsByLocalizacionId(Integer localizacionId);
}
