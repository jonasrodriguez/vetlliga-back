package com.vetlliga.refugiservice.dtos;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimalDto {

  private int id;
  private int numeroRegistro;
  private String tipo;
  private String nombre;
  private String chip;
  private LocalDate edad;
  private LocalDate entrada;
  private String sexo;
  private String raza;
  private String origen;
  private Integer localizacion;
  private Integer estado;
  private List<DesparasitacionDto> desparasitaciones;
  private List<HistorialDto> historial;
  private List<IntervencionDto> intervenciones;
  private List<PesoDto> pesos;
  private List<TestDto> tests;
  private List<VacunacionDto> vacunaciones;
}
