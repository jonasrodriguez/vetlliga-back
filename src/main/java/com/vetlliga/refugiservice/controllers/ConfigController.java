package com.vetlliga.refugiservice.controllers;

import com.vetlliga.refugiservice.dtos.AppConfigDto;
import com.vetlliga.refugiservice.dtos.LocalizacionDto;
import com.vetlliga.refugiservice.services.LocalizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

  private final LocalizacionService localizacionService;

  @GetMapping
  public AppConfigDto getConfig() {
    AppConfigDto config = new AppConfigDto();
    config.setLocalizaciones(localizacionService.getAllLocalizaciones());
    return config;
  }

  @PostMapping("/localizaciones")
  public ResponseEntity<LocalizacionDto> addLocalizacion(@RequestBody LocalizacionDto localizacion) {
    final var newLoc = localizacionService.nuevaLocalizacion(localizacion);
    return ResponseEntity.ok(newLoc);
  }

  @DeleteMapping("/localizaciones/{id}")
  public ResponseEntity<Void> deleteLocalizacion(@PathVariable Integer id) {
    localizacionService.borrarLocalizacion(id);
    return ResponseEntity.ok().build();
  }
}
