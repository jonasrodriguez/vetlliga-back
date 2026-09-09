package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.dtos.RecordatorioDto;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordatorioService {

  public static final int VENTANA_DESPARASITACION = 3;

  private final AnimalRepository animalRepository;

  public List<RecordatorioDto> getRecordatorios(TipoDesparasitacion tipo) {

    LocalDate fechaLimite = LocalDate.now().minusMonths(VENTANA_DESPARASITACION);

    return animalRepository.findPendientesDesparasitacion(tipo, fechaLimite)
        .stream()
        .map(row -> new RecordatorioDto(
            (Integer) row[0],
            (TipoAnimal) row[1],
            (String) row[2],
            (String) row[3],
            tipo,
            (LocalDate) row[4]
        ))
        .toList();
  }
}
