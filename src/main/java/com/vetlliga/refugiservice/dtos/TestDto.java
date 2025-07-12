package com.vetlliga.refugiservice.dtos;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestDto {

  private Integer id;
  private LocalDateTime fecha;
  private String tipo;
  private String resultado;
  private String lote;
}
