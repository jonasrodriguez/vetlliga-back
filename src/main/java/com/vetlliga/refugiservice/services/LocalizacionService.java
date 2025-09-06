package com.vetlliga.refugiservice.services;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.vetlliga.refugiservice.dtos.LocalizacionDto;
import com.vetlliga.refugiservice.mappers.LocalizacionMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.LocalizacionCache;
import com.vetlliga.refugiservice.repositories.LocalizacionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalizacionService {

  private final AnimalRepository animalRepository;
  private final LocalizacionRepository repository;
  private final LocalizacionCache cache;
  private final LocalizacionMapper mapper;

  public LocalizacionDto nuevaLocalizacion(LocalizacionDto dto) {
    final var existing = cache.getByName(dto.getNombre(), dto.getTipo());
    if (nonNull(existing)) {
      throw new IllegalStateException("Ya existe una localización con el nombre " + dto.getNombre());
    }

    final var newLoc = repository.save(mapper.toEntity(dto));
    cache.refresh();
    return mapper.toDto(newLoc);
  }

  public void borrarLocalizacion(Integer id) {
    var loc = cache.getById(id);
    if (isNull(loc)) {
      throw new IllegalStateException("No existe la localización con id " + id);
    }
    if (animalRepository.existsByLocalizacionId(id)) {
      throw new IllegalStateException("No se puede eliminar la localización " + loc.getNombre() + " porque hay animales asignados.");
    }

    repository.deleteById(id);
    cache.refresh();
  }

  public List<LocalizacionDto> getAllLocalizaciones() {
    return cache.getAll().stream().map(mapper::toDto).toList();
  }
}

