package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.dtos.LocalizacionDto;
import com.vetlliga.refugiservice.mappers.LocalizacionMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.LocalizacionCache;
import com.vetlliga.refugiservice.repositories.LocalizacionRepository;
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
    var newLoc = repository.save(mapper.toEntity(dto));
    cache.refresh();
    return mapper.toDto(newLoc);
  }

  public void borrarLocalizacion(Integer id) {
    var loc = cache.getById(id);
    if (loc == null) {
      throw new IllegalStateException("No existe la localización con id " + id);
    }
    if (animalRepository.existsByLocalizacionId(id)) {
      throw new IllegalStateException("No se puede eliminar la localización " + loc.getNombre() + " porque hay animales asignados.");
    }

    repository.deleteById(id);
    cache.refresh();
  }
}

