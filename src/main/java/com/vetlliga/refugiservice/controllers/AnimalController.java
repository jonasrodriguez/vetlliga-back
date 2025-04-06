package com.vetlliga.refugiservice.controllers;

import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.services.AnimalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping
  public ResponseEntity<List<AnimalDto>> listadoAnimales() {

    final var listadoAnimales = animalService.getAllAnimals();
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

}
