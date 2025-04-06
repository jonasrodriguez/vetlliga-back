package com.vetlliga.refugiservice.dtos;

import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Peso;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PesoDto {

  private Integer id;
  private LocalDate fecha;
  private Float peso;
}
