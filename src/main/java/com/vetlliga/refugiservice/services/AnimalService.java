package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.AnimalMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnimalService {

  private final AnimalRepository animalRepository;
  private final AnimalMapper animalMapper;

  public List<AnimalDto> getAllAnimals() {
    return animalRepository.findAll()
        .stream()
        .map(animalMapper::toDto)
        .toList();
  }

  public AnimalDto getAnimal(Integer id) {
    final var entity = animalRepository.findById(id).orElseThrow();
    return animalMapper.toDto(entity);
  }

  public AnimalDto altaAnimal(AnimalDto animal) {
    log.debug("Alta nuevo animal: {}", animal);

    final var animalEntity = animalMapper.toEntity(animal);
    animalEntity.setId(null);
    final var response = animalRepository.save(animalEntity);

    return animalMapper.toDto(response);
  }

  public AnimalDto actualizarAnimal(Integer id, AnimalDto animal) {
    log.debug("Actualizar animal: {}", animal);

    if (!animalRepository.existsById(id)) {
      throw new ResourceNotFoundException("Animal con id " + id + " no encontrado");
    }

    final var animalEntity = animalMapper.toEntity(animal);
    final var response = animalRepository.save(animalEntity);

    return animalMapper.toDto(response);
  }
}
