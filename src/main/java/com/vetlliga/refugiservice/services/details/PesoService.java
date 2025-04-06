package com.vetlliga.refugiservice.services.details;

import com.vetlliga.refugiservice.dtos.PesoDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.PesoMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.PesoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PesoService {

  private final AnimalRepository animalRepository;
  private final PesoRepository pesoRepository;
  private final PesoMapper pesoMapper;

  public PesoDto addPeso(Integer id, PesoDto peso) {
    log.debug("Nuevo peso: {} para animal: {}", peso, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var entity = pesoMapper.toEntity(peso, animalEntity);

    final var response = pesoRepository.save(entity);
    return pesoMapper.toDto(response);
  }

  public PesoDto updatePeso(Integer pesoId, PesoDto dto) {
    log.debug("Actualizar peso: {}", dto);

    final var entity = pesoRepository.findById(pesoId).orElseThrow(() ->
        new ResourceNotFoundException("Peso con id " + pesoId + " no encontrado"));

    entity.setPeso(dto.getPeso());
    entity.setFecha(dto.getFecha());

    final var response = pesoRepository.save(entity);
    return pesoMapper.toDto(response);
  }

  public void removePeso(Integer pesoId) {
    log.debug("Eliminar peso: {}", pesoId);

    final var entity = pesoRepository.findById(pesoId).orElseThrow(() ->
        new ResourceNotFoundException("Peso con id " + pesoId + " no encontrado"));
    pesoRepository.delete(entity);
  }
}
