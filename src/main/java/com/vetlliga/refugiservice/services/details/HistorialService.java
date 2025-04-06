package com.vetlliga.refugiservice.services.details;

import com.vetlliga.refugiservice.dtos.HistorialDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.HistorialMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.HistorialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HistorialService {

  private final AnimalRepository animalRepository;
  private final HistorialRepository historialRepository;
  private final HistorialMapper historialMapper;

  public HistorialDto addHistorial(Integer id, HistorialDto historial) {
    log.debug("Nuevo historial: {} para animal: {}", historial, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var entity = historialMapper.toEntity(historial, animalEntity);

    final var response = historialRepository.save(entity);
    return historialMapper.toDto(response);
  }

  public HistorialDto updateHistorial(Integer historialId, HistorialDto dto) {
    log.debug("Actualizar historial: {}", dto);

    final var entity = historialRepository.findById(historialId).orElseThrow(() ->
        new ResourceNotFoundException("Historial con id " + historialId + " no encontrado"));

    entity.setRevision(dto.getRevision());
    entity.setDiagnostico(dto.getDiagnostico());
    entity.setTratamiento(dto.getTratamiento());
    entity.setFecha(dto.getFecha());

    final var response = historialRepository.save(entity);
    return historialMapper.toDto(response);
  }

  public void removeHistorial(Integer historialId) {
    log.debug("Eliminar historial: {}", historialId);

    final var entity = historialRepository.findById(historialId).orElseThrow(() ->
        new ResourceNotFoundException("Historial con id " + historialId + " no encontrado"));
    historialRepository.delete(entity);
  }
}