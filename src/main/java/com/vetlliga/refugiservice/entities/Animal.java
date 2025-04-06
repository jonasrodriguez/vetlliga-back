package com.vetlliga.refugiservice.entities;

import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.LocalizacionGato;
import com.vetlliga.refugiservice.constants.LocalizacionPerro;
import com.vetlliga.refugiservice.constants.SexoAnimal;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "animales")
public class Animal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Integer id;
  private Integer numeroRegistro;
  @Enumerated(EnumType.STRING)
  private TipoAnimal tipo;
  private String nombre;
  private String chip;
  private LocalDate edad;
  private LocalDate entrada;
  @Enumerated(EnumType.STRING)
  private SexoAnimal sexo;
  private String raza;
  private String origen;
  @Enumerated(EnumType.STRING)
  private LocalizacionPerro localizacionPerro;
  @Enumerated(EnumType.STRING)
  private LocalizacionGato localizacionGato;
  @Enumerated(EnumType.STRING)
  private EstadoAnimal estado;
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Peso> pesos = new ArrayList<>();
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Test> tests = new ArrayList<>();
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Desparasitacion> desparasitaciones = new ArrayList<>();
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Vacunacion> vacunaciones = new ArrayList<>();
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Intervencion> intervenciones = new ArrayList<>();
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Historial> historial = new ArrayList<>();
}
