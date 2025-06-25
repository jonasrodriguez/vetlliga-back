package com.vetlliga.refugiservice.dtos;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ListadoAnimalesCriteria {

  private String search;

  private String tipo;
  private Integer estado;
  private LocalDate fechaEstado;
  private Integer localizacion;
  private LocalDate fechaLocalizacion;

  private LocalDate ultimaVacunaDesde;
  private LocalDate ultimaVacunaHasta;
  private LocalDate ultimaParasitoDesde;
  private LocalDate ultimaParasitoHasta;
  private LocalDate ultimoTestDesde;
  private LocalDate ultimoTestHasta;

  private String sortBy;
  private String sortDirection;
  private Integer page;
  private Integer pageSize;
}
