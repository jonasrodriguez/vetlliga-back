package com.vetlliga.refugiservice.dtos;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistorialDto {

  private Integer id;
  private LocalDate fecha;
  private String revision;
  private String diagnostico;
  private String tratamiento;
}
