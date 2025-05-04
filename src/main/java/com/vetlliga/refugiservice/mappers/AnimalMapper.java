package com.vetlliga.refugiservice.mappers;

import static java.util.Objects.isNull;

import com.vetlliga.refugiservice.constants.LocalizacionGato;
import com.vetlliga.refugiservice.constants.LocalizacionPerro;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.dtos.DesparasitacionDto;
import com.vetlliga.refugiservice.dtos.TestDto;
import com.vetlliga.refugiservice.dtos.VacunacionDto;
import com.vetlliga.refugiservice.entities.Animal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnimalMapper {

  private final DesparasitacionMapper desparasitacionMapper;
  private final HistorialMapper historialMapper;
  private final IntervencionMapper intervencionMapper;
  private final PesoMapper pesoMapper;
  private final TestMapper testMapper;
  private final VacunacionMapper vacunacionMapper;

  public AnimalDto toDto(Animal animal) {

    final var pesosDtos = mapToDtoList(animal.getPesos(), pesoMapper::toDto);
    final var desparasitacionesDtos = mapToDtoList(animal.getDesparasitaciones(), desparasitacionMapper::toDto);
    final var historialDtos = mapToDtoList(animal.getHistorial(), historialMapper::toDto);
    final var intervencionesDtos = mapToDtoList(animal.getIntervenciones(), intervencionMapper::toDto);
    final var testDtos = mapToDtoList(animal.getTests(), testMapper::toDto);
    final var vacunasDtos = mapToDtoList(animal.getVacunaciones(), vacunacionMapper::toDto);

    final var fechaUltimaVacunacion = vacunasDtos.stream()
        .map(VacunacionDto::getFecha)
        .max(LocalDate::compareTo)
        .orElse(null);

    final var fechaUltimaDesparasitacion = desparasitacionesDtos.stream()
        .map(DesparasitacionDto::getFecha)
        .max(LocalDate::compareTo)
        .orElse(null);

    final var fechaUltimoTest = testDtos.stream()
        .map(TestDto::getFecha)
        .max(LocalDate::compareTo)
        .orElse(null);

    return AnimalDto.builder()
        .id(animal.getId())
        .numeroRegistro(animal.getNumeroRegistro())
        .tipo(animal.getTipo())
        .nombre(animal.getNombre())
        .chip(animal.getChip())
        .fechaNacimiento(animal.getFechaNacimiento())
        .fechaEntrada(animal.getFechaEntrada())
        .sexo(animal.getSexo())
        .raza(animal.getRaza())
        .origen(animal.getOrigen())
        .enfermedades(animal.getEnfermedades())
        .localizacion(animal.getTipo().equals(TipoAnimal.PERRO) ? animal.getLocalizacionPerro().name() : animal.getLocalizacionGato().name())
        .fechaLocalizacion(animal.getFechaLocalizacion())
        .estado(animal.getEstado())
        .fechaEstado(animal.getFechaEstado())
        .desparasitaciones(desparasitacionesDtos)
        .historial(historialDtos)
        .intervenciones(intervencionesDtos)
        .pesos(pesosDtos)
        .vacunaciones(vacunasDtos)
        .tests(testDtos)
        .fechaUltimaVacunacion(fechaUltimaVacunacion)
        .fechaUltimaDesparasitacion(fechaUltimaDesparasitacion)
        .fechaUltimoTest(fechaUltimoTest)
        .build();
  }

  public Animal toEntity(AnimalDto dto) {

    final var locPerro = dto.getTipo().equals(TipoAnimal.PERRO) ? LocalizacionPerro.valueOf(dto.getLocalizacion()) : null;
    final var locGato = dto.getTipo().equals(TipoAnimal.GATO) ? LocalizacionGato.valueOf(dto.getLocalizacion()) : null;

    var animal = new Animal();
    animal.setId(dto.getId());
    animal.setNumeroRegistro(dto.getNumeroRegistro());
    animal.setTipo(dto.getTipo());
    animal.setNombre(dto.getNombre());
    animal.setChip(dto.getChip());
    animal.setFechaNacimiento(dto.getFechaNacimiento());
    animal.setFechaEntrada(dto.getFechaEntrada());
    animal.setSexo(dto.getSexo());
    animal.setRaza(dto.getRaza());
    animal.setOrigen(dto.getOrigen());
    animal.setEnfermedades(dto.getEnfermedades());
    animal.setLocalizacionPerro(locPerro);
    animal.setLocalizacionGato(locGato);
    animal.setFechaLocalizacion(dto.getFechaLocalizacion());
    animal.setEstado(dto.getEstado());
    animal.setFechaEstado(dto.getFechaEstado());

    return animal;
  }

  public Animal toEntityComplete(Animal entity, AnimalDto dto) {

    entity.setPesos(mapToEntityList(dto.getPesos(), pesoMapper::toEntity, entity));
    entity.setDesparasitaciones(mapToEntityList(dto.getDesparasitaciones(), desparasitacionMapper::toEntity, entity));
    entity.setHistorial(mapToEntityList(dto.getHistorial(), historialMapper::toEntity, entity));
    entity.setIntervenciones(mapToEntityList(dto.getIntervenciones(), intervencionMapper::toEntity, entity));
    entity.setTests(mapToEntityList(dto.getTests(), testMapper::toEntity, entity));
    entity.setVacunaciones(mapToEntityList(dto.getVacunaciones(), vacunacionMapper::toEntity, entity));

    return entity;
  }

  private <T, R> List<R> mapToDtoList(List<T> entities, Function<T, R> mapper) {
    if (isNull(entities)) {
      return List.of();
    }
    return entities.stream()
        .map(mapper)
        .toList();
  }

  private <T, R> List<R> mapToEntityList(List<T> dtos, BiFunction<T, Animal, R> mapper, Animal animal) {
    if (isNull(dtos)) {
      return List.of();
    }
    return dtos.stream()
        .map(dto -> mapper.apply(dto, animal))
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
