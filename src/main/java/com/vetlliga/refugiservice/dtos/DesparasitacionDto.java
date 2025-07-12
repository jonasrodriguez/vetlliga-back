package com.vetlliga.refugiservice.dtos;

import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DesparasitacionDto {

  private Integer id;
  private LocalDateTime fecha;
  private TipoDesparasitacion tipo;
  private String producto;
}
