package com.vetlliga.refugiservice.dtos;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestDto {

  private Integer id;
  private LocalDate fecha;
  private String tipo;
  private String resultado;
  private String lote;
}
