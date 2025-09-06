package com.vetlliga.refugiservice.controllers;

import com.vetlliga.refugiservice.dtos.LocalizacionDto;
import com.vetlliga.refugiservice.services.LocalizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localizaciones")
@RequiredArgsConstructor
public class LocalizacionController {

  private final LocalizacionService localizacionService;

  @GetMapping
  public List<LocalizacionDto> getAll() {
    return localizacionService.getAllLocalizaciones();
  }

  @PostMapping
  public LocalizacionDto create(@RequestBody LocalizacionDto dto) {
    return localizacionService.nuevaLocalizacion(dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    localizacionService.borrarLocalizacion(id);
  }
}