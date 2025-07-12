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

  private LocalDate vacunaDesde;
  private LocalDate vacunaHasta;
  private LocalDate desparasitoInternaDesde;
  private LocalDate desparasitoInternaHasta;
  private LocalDate desparasitoExternaDesde;
  private LocalDate desparasitoExternaHasta;
  private LocalDate testDesde;
  private LocalDate testHasta;

  private String sortBy;
  private String sortDirection;
  private Integer page;
  private Integer pageSize;
}
