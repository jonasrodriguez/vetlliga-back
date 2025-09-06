package com.vetlliga.refugiservice.dtos;

import java.util.List;
import lombok.Data;

@Data
public class AppConfigDto {
  private List<LocalizacionDto> localizaciones;
}
