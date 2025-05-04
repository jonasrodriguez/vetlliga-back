package com.vetlliga.refugiservice.dtos;

import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.SexoAnimal;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimalDto {

  private int id;
  private int numeroRegistro;
  private TipoAnimal tipo;
  private String nombre;
  private String chip;
  private LocalDate fechaNacimiento;
  private LocalDate fechaEntrada;
  private SexoAnimal sexo;
  private String raza;
  private String origen;
  private String enfermedades;
  private String localizacion;
  private LocalDate fechaLocalizacion;
  private EstadoAnimal estado;
  private LocalDate fechaEstado;
  private LocalDate fechaUltimaVacunacion;
  private LocalDate fechaUltimaDesparasitacion;
  private LocalDate fechaUltimoTest;
  private List<DesparasitacionDto> desparasitaciones;
  private List<HistorialDto> historial;
  private List<IntervencionDto> intervenciones;
  private List<PesoDto> pesos;
  private List<TestDto> tests;
  private List<VacunacionDto> vacunaciones;
}
