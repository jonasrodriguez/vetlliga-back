package com.vetlliga.refugiservice.dtos;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalizacionDto {

  private Integer id;
  private TipoAnimal tipo;
  private String nombre;
}
