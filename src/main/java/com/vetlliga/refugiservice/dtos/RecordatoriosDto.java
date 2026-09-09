package com.vetlliga.refugiservice.dtos;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordatoriosDto {

  private List<RecordatorioDto> desparasitacionesInternas;
  private List<RecordatorioDto> desparasitacionesExternas;
}
