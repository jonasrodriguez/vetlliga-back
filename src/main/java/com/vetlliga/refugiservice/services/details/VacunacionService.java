package com.vetlliga.refugiservice.services.details;

import com.vetlliga.refugiservice.dtos.VacunacionDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.VacunacionMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.VacunacionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VacunacionService {

  private final AnimalRepository animalRepository;
  private final VacunacionRepository vacunacionRepository;
  private final VacunacionMapper vacunacionMapper;

  public VacunacionDto addVacunacion(Integer id, VacunacionDto vacunacion) {
    log.debug("Nueva vacunación: {} para animal: {}", vacunacion, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var entity = vacunacionMapper.toEntity(vacunacion, animalEntity);

    final var response = vacunacionRepository.save(entity);
    return vacunacionMapper.toDto(response);
  }

  public VacunacionDto updateVacunacion(Integer vacunacionId, VacunacionDto dto) {
    log.debug("Actualizar vacunación: {}", dto);

    final var entity = vacunacionRepository.findById(vacunacionId).orElseThrow(() ->
        new ResourceNotFoundException("Vacunación con id " + vacunacionId + " no encontrado"));

    entity.setFecha(dto.getFecha());
    entity.setTipo(dto.getTipo());
    entity.setMarca(dto.getMarca());

    final var response = vacunacionRepository.save(entity);
    return vacunacionMapper.toDto(response);
  }

  public void removeVacunacion(Integer vacunacionId) {
    log.debug("Eliminar vacunación: {}", vacunacionId);

    final var entity = vacunacionRepository.findById(vacunacionId).orElseThrow(() ->
        new ResourceNotFoundException("Vacunación con id " + vacunacionId + " no encontrado"));
    vacunacionRepository.delete(entity);
  }
}