package com.vetlliga.refugiservice.controllers;

import com.vetlliga.refugiservice.dtos.DesparasitacionDto;
import com.vetlliga.refugiservice.dtos.HistorialDto;
import com.vetlliga.refugiservice.dtos.IntervencionDto;
import com.vetlliga.refugiservice.dtos.PesoDto;
import com.vetlliga.refugiservice.dtos.TestDto;
import com.vetlliga.refugiservice.dtos.VacunacionDto;
import com.vetlliga.refugiservice.services.details.DesparasitacionService;
import com.vetlliga.refugiservice.services.details.HistorialService;
import com.vetlliga.refugiservice.services.details.IntervencionService;
import com.vetlliga.refugiservice.services.details.PesoService;
import com.vetlliga.refugiservice.services.details.TestService;
import com.vetlliga.refugiservice.services.details.VacunacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/animales/{id}")
@RequiredArgsConstructor
public class AnimalDetailsController {

  private final PesoService pesoService;
  private final IntervencionService intervencionService;
  private final TestService testService;
  private final VacunacionService vacunacionService;
  private final DesparasitacionService desparasitacionService;
  private final HistorialService historialService;

  @PostMapping("/peso")
  public ResponseEntity<PesoDto> addPeso(@PathVariable Integer id, @RequestBody PesoDto peso) {

    return ResponseEntity.ok(pesoService.addPeso(id, peso));
  }

  @PutMapping("/peso/{pesoId}")
  public ResponseEntity<PesoDto> updatePeso(@PathVariable Integer pesoId, @RequestBody PesoDto peso) {

    return ResponseEntity.ok(pesoService.updatePeso(pesoId, peso));
  }

  @DeleteMapping("/peso/{pesoId}")
  public ResponseEntity<Void> removePeso(@PathVariable Integer pesoId) {
    pesoService.removePeso(pesoId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/intervencion")
  public ResponseEntity<IntervencionDto> addIntervencion(@PathVariable Integer id, @RequestBody IntervencionDto intervencion) {
    return ResponseEntity.ok(intervencionService.addIntervencion(id, intervencion));
  }

  @PutMapping("/intervencion/{intervencionId}")
  public ResponseEntity<IntervencionDto> updateIntervencion(@PathVariable Integer intervencionId, @RequestBody IntervencionDto intervencion) {
    return ResponseEntity.ok(intervencionService.updateIntervencion(intervencionId, intervencion));
  }

  @DeleteMapping("/intervencion/{intervencionId}")
  public ResponseEntity<Void> removeIntervencion(@PathVariable Integer intervencionId) {
    intervencionService.removeIntervencion(intervencionId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/test")
  public ResponseEntity<TestDto> addTest(@PathVariable Integer id, @RequestBody TestDto test) {
    return ResponseEntity.ok(testService.addTest(id, test));
  }

  @PutMapping("/test/{testId}")
  public ResponseEntity<TestDto> updateTest(@PathVariable Integer testId, @RequestBody TestDto test) {
    return ResponseEntity.ok(testService.updateTest(testId, test));
  }

  @DeleteMapping("/test/{testId}")
  public ResponseEntity<Void> removeTest(@PathVariable Integer testId) {
    testService.removeTest(testId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/vacunacion")
  public ResponseEntity<VacunacionDto> addVacunacion(@PathVariable Integer id, @RequestBody VacunacionDto vacunacion) {
    return ResponseEntity.ok(vacunacionService.addVacunacion(id, vacunacion));
  }

  @PutMapping("/vacunacion/{vacunacionId}")
  public ResponseEntity<VacunacionDto> updateVacunacion(@PathVariable Integer vacunacionId, @RequestBody VacunacionDto vacunacion) {
    return ResponseEntity.ok(vacunacionService.updateVacunacion(vacunacionId, vacunacion));
  }

  @DeleteMapping("/vacunacion/{vacunacionId}")
  public ResponseEntity<Void> removeVacunacion(@PathVariable Integer vacunacionId) {
    vacunacionService.removeVacunacion(vacunacionId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/desparasitacion")
  public ResponseEntity<DesparasitacionDto> addDesparasitacion(@PathVariable Integer id, @RequestBody DesparasitacionDto desparasitacion) {
    return ResponseEntity.ok(desparasitacionService.addDesparasitacion(id, desparasitacion));
  }

  @PutMapping("/desparasitacion/{desparasitacionId}")
  public ResponseEntity<DesparasitacionDto> updateDesparasitacion(@PathVariable Integer desparasitacionId, @RequestBody DesparasitacionDto desparasitacion) {
    return ResponseEntity.ok(desparasitacionService.updateDesparasitacion(desparasitacionId, desparasitacion));
  }

  @DeleteMapping("/desparasitacion/{desparasitacionId}")
  public ResponseEntity<Void> removeDesparasitacion(@PathVariable Integer desparasitacionId) {
    desparasitacionService.removeDesparasitacion(desparasitacionId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/historial")
  public ResponseEntity<HistorialDto> addHistorial(@PathVariable Integer id, @RequestBody HistorialDto historial) {
    return ResponseEntity.ok(historialService.addHistorial(id, historial));
  }

  @PutMapping("/historial/{historialId}")
  public ResponseEntity<HistorialDto> updateHistorial(@PathVariable Integer historialId, @RequestBody HistorialDto historial) {
    return ResponseEntity.ok(historialService.updateHistorial(historialId, historial));
  }

  @DeleteMapping("/historial/{historialId}")
  public ResponseEntity<Void> removeHistorial(@PathVariable Integer historialId) {
    historialService.removeHistorial(historialId);
    return ResponseEntity.noContent().build();
  }

}
