package com.vetlliga.refugiservice.mappers;

import static java.util.Objects.isNull;

import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.LocalizacionGato;
import com.vetlliga.refugiservice.constants.LocalizacionPerro;
import com.vetlliga.refugiservice.constants.SexoAnimal;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.entities.Animal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    return AnimalDto.builder()
        .id(animal.getId())
        .numeroRegistro(animal.getNumeroRegistro())
        .tipo(animal.getTipo().getValue())
        .nombre(animal.getNombre())
        .chip(animal.getChip())
        .edad(animal.getEdad())
        .entrada(animal.getEntrada())
        .sexo(animal.getSexo().getValue())
        .raza(animal.getRaza())
        .origen(animal.getOrigen())
        .localizacion(animal.getTipo().equals(TipoAnimal.PERRO) ? animal.getLocalizacionPerro().getValue() : animal.getLocalizacionGato().getValue())
        .estado(animal.getEstado().getValue())
        .desparasitaciones(desparasitacionesDtos)
        .historial(historialDtos)
        .intervenciones(intervencionesDtos)
        .pesos(pesosDtos)
        .vacunaciones(vacunasDtos)
        .tests(testDtos)
        .build();
  }

  public Animal toEntity(AnimalDto dto) {

    final var tipo = TipoAnimal.fromValue(dto.getTipo());
    final var locPerro = tipo.equals(TipoAnimal.PERRO) ? LocalizacionPerro.fromValue(dto.getLocalizacion()) : null;
    final var locGato = tipo.equals(TipoAnimal.GATO) ? LocalizacionGato.fromValue(dto.getLocalizacion()) : null;

    var animal = new Animal();
    animal.setId(dto.getId());
    animal.setNumeroRegistro(dto.getNumeroRegistro());
    animal.setTipo(tipo);
    animal.setNombre(dto.getNombre());
    animal.setChip(dto.getChip());
    animal.setEdad(dto.getEdad());
    animal.setEntrada(dto.getEntrada());
    animal.setSexo(SexoAnimal.fromValue(dto.getSexo()));
    animal.setRaza(dto.getRaza());
    animal.setOrigen(dto.getOrigen());
    animal.setLocalizacionPerro(locPerro);
    animal.setLocalizacionGato(locGato);
    animal.setEstado(EstadoAnimal.fromValue(dto.getEstado()));

    animal.setPesos(mapToEntityList(dto.getPesos(), pesoMapper::toEntity, animal));
    animal.setDesparasitaciones(mapToEntityList(dto.getDesparasitaciones(), desparasitacionMapper::toEntity, animal));
    animal.setHistorial(mapToEntityList(dto.getHistorial(), historialMapper::toEntity, animal));
    animal.setIntervenciones(mapToEntityList(dto.getIntervenciones(), intervencionMapper::toEntity, animal));
    animal.setTests(mapToEntityList(dto.getTests(), testMapper::toEntity, animal));
    animal.setVacunaciones(mapToEntityList(dto.getVacunaciones(), vacunacionMapper::toEntity, animal));

    return animal;
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
        .toList();
  }
}
