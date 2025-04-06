package com.vetlliga.refugiservice.services.details;

import com.vetlliga.refugiservice.dtos.DesparasitacionDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.DesparasitacionMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.DesparasitacionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DesparasitacionService {

  private final AnimalRepository animalRepository;
  private final DesparasitacionRepository desparasitacionRepository;
  private final DesparasitacionMapper desparasitacionMapper;

  public DesparasitacionDto addDesparasitacion(Integer id, DesparasitacionDto desparasitacion) {
    log.debug("Nueva desparasitación: {} para animal: {}", desparasitacion, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var entity = desparasitacionMapper.toEntity(desparasitacion, animalEntity);

    final var response = desparasitacionRepository.save(entity);
    return desparasitacionMapper.toDto(response);
  }

  public DesparasitacionDto updateDesparasitacion(Integer desparasitacionId, DesparasitacionDto dto) {
    log.debug("Actualizar desparasitación: {}", dto);

    final var entity = desparasitacionRepository.findById(desparasitacionId).orElseThrow(() ->
        new ResourceNotFoundException("Desparasitación con id " + desparasitacionId + " no encontrado"));

    entity.setFecha(dto.getFecha());
    entity.setProducto(dto.getProducto());

    final var response = desparasitacionRepository.save(entity);
    return desparasitacionMapper.toDto(response);
  }

  public void removeDesparasitacion(Integer desparasitacionId) {
    log.debug("Eliminar desparasitación: {}", desparasitacionId);

    final var entity = desparasitacionRepository.findById(desparasitacionId).orElseThrow(() ->
        new ResourceNotFoundException("Desparasitación con id " + desparasitacionId + " no encontrado"));
    desparasitacionRepository.delete(entity);
  }
}