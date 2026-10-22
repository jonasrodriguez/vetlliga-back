package com.vetlliga.refugiservice.dtos;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RecordatorioDto {

  private int id;
  private TipoAnimal tipo;
  private String nombre;
  private Float peso;
  private TipoDesparasitacion tipoDesparasitacion;
  private LocalDate fechaDesparasitacion;
}
