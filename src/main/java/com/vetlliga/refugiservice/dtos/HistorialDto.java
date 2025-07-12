package com.vetlliga.refugiservice.dtos;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistorialDto {

  private Integer id;
  private LocalDateTime fecha;
  private String descripcion;
}
