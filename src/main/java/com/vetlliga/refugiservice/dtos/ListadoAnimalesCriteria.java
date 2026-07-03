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

  private LocalDate vacuna;
  private LocalDate desparasitoInterna;
  private LocalDate desparasitoExterna;
  private LocalDate test;

  private String sortBy;
  private String sortDirection;
  private Integer page;
  private Integer pageSize;
}
