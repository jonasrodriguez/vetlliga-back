package com.vetlliga.refugiservice.services;

import static java.util.stream.Collectors.toList;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.dtos.RecordatorioDto;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordatorioService {

  private static final int PERRO_EXTERNA_MESES = 1;
  private static final int PERRO_INTERNA_MESES = 3;
  private static final int GATO_EXTERNA_MESES = 6;
  private static final int GATO_INTERNA_MESES = 6;

  private final AnimalRepository animalRepository;

  public List<RecordatorioDto> getRecordatorios(TipoDesparasitacion tipo) {

    int ventanaPerro = tipo == TipoDesparasitacion.EXTERNA ? PERRO_EXTERNA_MESES : PERRO_INTERNA_MESES;
    int ventanaGato = tipo == TipoDesparasitacion.EXTERNA ? GATO_EXTERNA_MESES : GATO_INTERNA_MESES;

    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime limitePerro = ahora.minusMonths(ventanaPerro);
    LocalDateTime limiteGato = ahora.minusMonths(ventanaGato);

    return animalRepository.findPendientesDesparasitacion(tipo, limitePerro, limiteGato).stream()
        .map(row -> new RecordatorioDto(
            (Integer) row[0],
            (TipoAnimal) row[1],
            (String) row[2],
            (Float) row[3],
            tipo,
            row[4] != null ? ((LocalDateTime) row[4]).toLocalDate() : null
        ))
        .toList();
  }
}
