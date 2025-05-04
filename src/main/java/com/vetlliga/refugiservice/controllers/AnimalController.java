package com.vetlliga.refugiservice.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.dtos.ListadoAnimalesCriteria;
import com.vetlliga.refugiservice.services.AnimalService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/animales")
@RequiredArgsConstructor
public class AnimalController {

  private final AnimalService animalService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public ResponseEntity<List<AnimalDto>> listadoAnimales(ListadoAnimalesCriteria criteria) {

    final var sort = Sort.by(
        Sort.Direction.fromString(criteria.getSortDirection() != null ? criteria.getSortDirection() : "asc"),
        criteria.getSortBy() != null ? criteria.getSortBy() : "fechaEntrada");

    final var page = criteria.getPage() != null ? criteria.getPage() : 0;
    final var size = criteria.getPageSize() != null ? criteria.getPageSize() : 25;

    final var pageable = PageRequest.of(page, size, sort);

    final var listadoAnimales = animalService.listAnimals(criteria, pageable);
    return ResponseEntity.ok(listadoAnimales);
  }

  @PostMapping
  public ResponseEntity<AnimalDto> createAnimal(@RequestBody AnimalDto animal) {

    final var nuevaAlta = animalService.altaAnimal(animal);
    return ResponseEntity.ok(nuevaAlta);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AnimalDto> getAnimal(@PathVariable Integer id) {

    final var animal = animalService.getAnimal(id);
    return ResponseEntity.ok(animal);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AnimalDto> updateAnimal(@PathVariable Integer id, @RequestBody AnimalDto animal) {

    final var animalActualizado = animalService.actualizarAnimal(id, animal);
    return ResponseEntity.ok(animalActualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> borradoAnimal(@PathVariable Integer id) {

    animalService.borradoAnimal(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/load-sample-data")
  public ResponseEntity<?> loadSampleAnimals() throws IOException {
    InputStream inputStream = getClass().getResourceAsStream("/data/animals_data.json");
    List<AnimalDto> animals = objectMapper.readValue(inputStream, new TypeReference<>() {
    });
    animals.forEach(animalService::altaAnimal);
    animalService.randomizerAnimals();

    return ResponseEntity.ok("Loaded " + animals.size() + " sample animals");
  }

}
