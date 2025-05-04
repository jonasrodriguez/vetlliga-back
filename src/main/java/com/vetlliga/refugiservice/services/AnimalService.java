package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.dtos.ListadoAnimalesCriteria;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.AnimalMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.specifications.AnimalSpecifications;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnimalService {

  private final AnimalRepository animalRepository;
  private final AnimalMapper animalMapper;

  public List<AnimalDto> listAnimals(ListadoAnimalesCriteria criteria, Pageable pageable) {
    final var specification = AnimalSpecifications.filterByCriteria(criteria);

    return animalRepository.findAll(specification).stream()
        .map(animalMapper::toDto)
        .toList();
  }

  public AnimalDto getAnimal(Integer id) {
    final var entity = animalRepository.findById(id).orElseThrow();
    return animalMapper.toDto(entity);
  }

  public AnimalDto altaAnimal(AnimalDto animal) {
    log.debug("Alta nuevo animal: {}", animal);

    final var now = LocalDate.now();

    final var animalEntity = animalMapper.toEntity(animal);

    animalEntity.setId(null);
    animalEntity.setFechaEntrada(now);
    animalEntity.setFechaEstado(now);
    animalEntity.setFechaLocalizacion(now);
    final var savedAnimal = animalRepository.save(animalEntity);

    // Completamos la creacion con la subentities despues del guardado inicial
    var entityComplete = animalMapper.toEntityComplete(savedAnimal, animal);
    final var completeAnimal = animalRepository.save(entityComplete);

    return animalMapper.toDto(completeAnimal);
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

  public void borradoAnimal(Integer id) {
    log.debug("Borrado animal con id: {}", id);

    if (!animalRepository.existsById(id)) {
      throw new ResourceNotFoundException("Animal con id " + id + " no encontrado");
    }

    animalRepository.deleteById(id);
  }

  public void randomizerAnimals() {
    log.debug("Randomizer animals");

    var animales = animalRepository.findAll();
    animales.forEach(a -> {
      final var fechaRandom = LocalDate.now().minusDays((long) (Math.random() * 365));
      a.setFechaEntrada(fechaRandom);
      a.setFechaEstado(fechaRandom);
      a.setFechaLocalizacion(fechaRandom);
    });
    animalRepository.saveAll(animales);
  }
}
