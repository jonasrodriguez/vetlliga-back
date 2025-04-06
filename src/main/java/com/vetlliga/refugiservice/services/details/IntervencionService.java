package com.vetlliga.refugiservice.services.details;

import com.vetlliga.refugiservice.dtos.IntervencionDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.IntervencionMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.IntervencionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IntervencionService {

  private final AnimalRepository animalRepository;
  private final IntervencionRepository intervencionRepository;
  private final IntervencionMapper intervencionMapper;

  public IntervencionDto addIntervencion(Integer id, IntervencionDto intervencion) {
    log.debug("Nueva intervención: {} para animal: {}", intervencion, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var entity = intervencionMapper.toEntity(intervencion, animalEntity);

    final var response = intervencionRepository.save(entity);
    return intervencionMapper.toDto(response);
  }

  public IntervencionDto updateIntervencion(Integer intervencionId, IntervencionDto dto) {
    log.debug("Actualizar intervención: {}", dto);

    final var entity = intervencionRepository.findById(intervencionId).orElseThrow(() ->
        new ResourceNotFoundException("Intervención con id " + intervencionId + " no encontrado"));

    entity.setTipo(dto.getTipo());
    entity.setFecha(dto.getFecha());

    final var response = intervencionRepository.save(entity);
    return intervencionMapper.toDto(response);
  }

  public void removeIntervencion(Integer intervencionId) {
    log.debug("Eliminar intervención: {}", intervencionId);

    final var entity = intervencionRepository.findById(intervencionId).orElseThrow(() ->
        new ResourceNotFoundException("Intervención con id " + intervencionId + " no encontrado"));
    intervencionRepository.delete(entity);
  }
}