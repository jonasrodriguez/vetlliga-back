package com.vetlliga.refugiservice.controllers;

import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.dtos.RecordatorioDto;
import com.vetlliga.refugiservice.dtos.RecordatoriosDto;
import com.vetlliga.refugiservice.services.RecordatorioService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recordatorios")
@AllArgsConstructor
public class RecordatorioControllers {

  private final RecordatorioService recordatorioService;

  @GetMapping
  public RecordatoriosDto getAll() {

    List<RecordatorioDto> internas = recordatorioService.getRecordatorios(TipoDesparasitacion.INTERNA);
    List<RecordatorioDto> externas = recordatorioService.getRecordatorios(TipoDesparasitacion.EXTERNA);

    return RecordatoriosDto.builder()
        .desparasitacionesInternas(internas)
        .desparasitacionesExternas(externas)
        .build();
  }
}
