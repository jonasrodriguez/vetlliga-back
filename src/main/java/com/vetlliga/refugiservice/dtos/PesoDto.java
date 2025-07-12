package com.vetlliga.refugiservice.dtos;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PesoDto {

  private Integer id;
  private LocalDateTime fecha;
  private Float peso;
}
