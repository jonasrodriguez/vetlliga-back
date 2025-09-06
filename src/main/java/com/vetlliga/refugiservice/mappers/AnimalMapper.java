package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.dtos.DesparasitacionDto;
import com.vetlliga.refugiservice.dtos.DocumentoDto;
import com.vetlliga.refugiservice.dtos.HistorialDto;
import com.vetlliga.refugiservice.dtos.IntervencionDto;
import com.vetlliga.refugiservice.dtos.PesoDto;
import com.vetlliga.refugiservice.dtos.TestDto;
import com.vetlliga.refugiservice.dtos.VacunacionDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.repositories.LocalizacionCache;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
  private final DocumentosMapper documentosMapper;
  private final LocalizacionCache localizacionCache;

  public AnimalDto toDto(Animal animal) {

    final var pesosDtos = mapAndSortByDateDesc(animal.getPesos(), pesoMapper::toDto, PesoDto::getFecha);
    final var desparasitacionesDtos = mapAndSortByDateDesc(animal.getDesparasitaciones(), desparasitacionMapper::toDto, DesparasitacionDto::getFecha);
    final var testDtos = mapAndSortByDateDesc(animal.getTests(), testMapper::toDto, TestDto::getFecha);
    final var vacunasDtos = mapAndSortByDateDesc(animal.getVacunaciones(), vacunacionMapper::toDto, VacunacionDto::getFecha);
    final var intervencionesDtos = mapAndSortByDateDesc(animal.getIntervenciones(), intervencionMapper::toDto, IntervencionDto::getFecha);
    final var historialDtos = mapAndSortByDateDesc(animal.getHistorial(), historialMapper::toDto, HistorialDto::getFecha);
    final var documentosDtos = mapAndSortByDateDesc(animal.getDocumentos(), documentosMapper::toDto, DocumentoDto::getFecha);

    final var ultimaVacunacion = vacunasDtos.stream()
        .max(Comparator.comparing(VacunacionDto::getFecha));

    final var ultimaDesparasitacionInterna = desparasitacionesDtos.stream()
        .filter(d -> d.getTipo().equals(TipoDesparasitacion.INTERNA))
        .max(Comparator.comparing(DesparasitacionDto::getFecha));

    final var ultimaDesparasitacionExterna = desparasitacionesDtos.stream()
        .filter(d -> d.getTipo().equals(TipoDesparasitacion.EXTERNA))
        .max(Comparator.comparing(DesparasitacionDto::getFecha));

    final var ultimoPeso = pesosDtos.stream()
        .map(PesoDto::getPeso)
        .max(Float::compareTo)
        .orElse(null);

    var response = AnimalDto.builder()
        .id(animal.getId())
        .numeroRegistro(animal.getNumeroRegistro())
        .tipo(animal.getTipo())
        .nombre(animal.getNombre())
        .chip(animal.getChip())
        .fechaNacimiento(animal.getFechaNacimiento())
        .fechaEntrada(animal.getFechaEntrada())
        .sexo(animal.getSexo())
        .raza(animal.getRaza())
        .color(animal.getColor())
        .origen(animal.getOrigen())
        .enfermedades(animal.getEnfermedades())
        .antecedentes(animal.getAntecedentes())
        .localizacion(animal.getLocalizacion().getId())
        .fechaLocalizacion(animal.getFechaLocalizacion())
        .estado(animal.getEstado())
        .fechaEstado(animal.getFechaEstado())
        .desparasitaciones(desparasitacionesDtos)
        .historial(historialDtos)
        .intervenciones(intervencionesDtos)
        .pesos(pesosDtos)
        .vacunaciones(vacunasDtos)
        .tests(testDtos)
        .documentos(documentosDtos)
        .fechaCreacion(animal.getFechaCreacion())
        .fechaModificacion(animal.getFechaModificacion())
        .usuarioCreacion(animal.getUsuarioCreacion())
        .usuarioModificacion(animal.getUsuarioModificacion())
        .build();

    response.setUltimoPeso(ultimoPeso);

    ultimaVacunacion.ifPresent(vacuna -> {
      response.setFechaUltimaVacunacion(vacuna.getFecha().toLocalDate());
      response.setTipoUltimaVacunacion(vacuna.getTipo() + " - " + vacuna.getProducto());
    });

    ultimaDesparasitacionInterna.ifPresent(desparasitacion -> {
      response.setFechaUltimaDesparasitacionInterna(desparasitacion.getFecha().toLocalDate());
      response.setTipoUltimaDesparasitacionInterna(desparasitacion.getProducto());
    });

    ultimaDesparasitacionExterna.ifPresent(desparasitacion -> {
      response.setFechaUltimaDesparasitacionExterna(desparasitacion.getFecha().toLocalDate());
      response.setTipoUltimaDesparasitacionExterna(desparasitacion.getProducto());
    });
    return response;
  }

  public Animal toEntity(AnimalDto dto) {

    final var localizacion = localizacionCache.getById(dto.getLocalizacion());

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
    animal.setColor(dto.getColor());
    animal.setOrigen(dto.getOrigen());
    animal.setEnfermedades(dto.getEnfermedades());
    animal.setAntecedentes(dto.getAntecedentes());

    animal.setLocalizacion(localizacion);
    animal.setFechaLocalizacion(dto.getFechaLocalizacion());
    animal.setEstado(dto.getEstado());
    animal.setFechaEstado(dto.getFechaEstado());

    return animal;
  }

  public void updateEntityFromDto(AnimalDto dto, Animal entity) {

    final var localizacion = localizacionCache.getById(dto.getLocalizacion());

    entity.setNumeroRegistro(dto.getNumeroRegistro());
    entity.setTipo(dto.getTipo());
    entity.setNombre(dto.getNombre());
    entity.setChip(dto.getChip());
    entity.setFechaNacimiento(dto.getFechaNacimiento());
    entity.setFechaEntrada(dto.getFechaEntrada());
    entity.setSexo(dto.getSexo());
    entity.setRaza(dto.getRaza());
    entity.setColor(dto.getColor());
    entity.setOrigen(dto.getOrigen());
    entity.setEnfermedades(dto.getEnfermedades());
    entity.setAntecedentes(dto.getAntecedentes());

    entity.setEstado(dto.getEstado());
    entity.setFechaEstado(dto.getFechaEstado());
    entity.setLocalizacion(localizacion);
    entity.setFechaLocalizacion(dto.getFechaLocalizacion());
  }

  private <T, D> List<D> mapAndSortByDateDesc(List<T> list, Function<T, D> mapper, Function<D, LocalDateTime> dateGetter) {
    return list.stream()
        .map(mapper)
        .sorted(Comparator.comparing(dateGetter).reversed())
        .toList();
  }
}
