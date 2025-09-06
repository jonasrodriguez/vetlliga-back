package com.vetlliga.refugiservice.services;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.dtos.ListadoAnimalesCriteria;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.AnimalMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.LocalizacionRepository;
import com.vetlliga.refugiservice.specifications.AnimalSpecifications;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnimalService {

  private final AnimalRepository animalRepository;
  private final LocalizacionRepository localizacionRepository;
  private final AnimalMapper animalMapper;
  private final ObjectMapper objectMapper;

  public Page<AnimalDto> listAnimals(ListadoAnimalesCriteria criteria, Pageable pageable) {
    final var specification = AnimalSpecifications.filterByCriteria(criteria);

    var page = animalRepository.findAll(specification, pageable);
    return page.map(animalMapper::toDto);
  }

  public AnimalDto getAnimal(Integer id) {
    final var entity = animalRepository.findById(id).orElseThrow();
    return animalMapper.toDto(entity);
  }

  public AnimalDto altaAnimal(AnimalDto animal, String username) {
    log.debug("Alta nuevo animal: {}", animal);

    final var animalEntity = animalMapper.toEntity(animal);

    animalEntity.setId(null);
    animalEntity.setFechaCreacion(LocalDate.now());
    animalEntity.setUsuarioCreacion(username);

    final var savedAnimal = animalRepository.save(animalEntity);
    return animalMapper.toDto(savedAnimal);
  }

  public AnimalDto actualizarAnimal(Integer id, AnimalDto animal, String username) {
    log.debug("Actualizar animal: {}", animal);

    final var existingAnimal = animalRepository.findById(id).orElse(null);
    if (isNull(existingAnimal)) {
      throw new ResourceNotFoundException("Animal con id " + id + " no encontrado");
    }

    animalMapper.updateEntityFromDto(animal, existingAnimal);
    existingAnimal.setUsuarioModificacion(username);
    existingAnimal.setFechaModificacion(LocalDate.now());
    final var response = animalRepository.save(existingAnimal);

    return animalMapper.toDto(response);
  }

  public void borradoAnimal(Integer id) {
    log.debug("Borrado animal con id: {}", id);

    if (!animalRepository.existsById(id)) {
      throw new ResourceNotFoundException("Animal con id " + id + " no encontrado");
    }

    animalRepository.deleteById(id);
  }

  public void toggleActive(Integer id, boolean active) {
    var animal = animalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    animal.setActivo(active);
    animalRepository.save(animal);
  }

  public List<AnimalDto> loadSampleAnimals(String username) throws IOException {
    log.debug("Cargando animales de ejemplo...");

    final var localizaciones = localizacionRepository.findAll();
    final var localizacionesGato = localizaciones.stream().filter(l -> l.getTipo().equals(TipoAnimal.GATO)).toList();
    final var localizacionesPerro = localizaciones.stream().filter(l -> l.getTipo().equals(TipoAnimal.PERRO)).toList();

    InputStream inputStream = getClass().getResourceAsStream("/data/animals_data.json");
    List<AnimalDto> animals = objectMapper.readValue(inputStream, new TypeReference<>() {
    });
    animals.forEach(a -> {
      final var fechaRandom = LocalDate.now().minusDays((long) (Math.random() * 365));
      a.setFechaEntrada(fechaRandom);
      a.setFechaEstado(fechaRandom);
      a.setFechaLocalizacion(fechaRandom);

      var randomEstado = EstadoAnimal.values()[new Random().nextInt(EstadoAnimal.values().length)];
      a.setEstado(randomEstado);

      if (a.getTipo().equals(TipoAnimal.GATO) && !localizacionesGato.isEmpty()) {
        var randomLocalizacion = localizacionesGato.get(new Random().nextInt(localizacionesGato.size()));
        a.setLocalizacion(randomLocalizacion.getId());
      } else if (a.getTipo().equals(TipoAnimal.PERRO) && !localizacionesPerro.isEmpty()) {
        var randomLocalizacion = localizacionesPerro.get(new Random().nextInt(localizacionesPerro.size()));
        a.setLocalizacion(randomLocalizacion.getId());
      }

      altaAnimal(a, username);
    });

    return animals;
  }
}
