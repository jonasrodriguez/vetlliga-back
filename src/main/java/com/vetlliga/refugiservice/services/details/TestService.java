package com.vetlliga.refugiservice.services.details;

import com.vetlliga.refugiservice.dtos.TestDto;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.TestMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.TestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TestService {

  private final AnimalRepository animalRepository;
  private final TestRepository testRepository;
  private final TestMapper testMapper;

  public TestDto addTest(Integer id, TestDto test) {
    log.debug("Nuevo test: {} para animal: {}", test, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var entity = testMapper.toEntity(test, animalEntity);

    final var response = testRepository.save(entity);
    return testMapper.toDto(response);
  }

  public TestDto updateTest(Integer testId, TestDto dto) {
    log.debug("Actualizar test: {}", dto);

    final var entity = testRepository.findById(testId).orElseThrow(() ->
        new ResourceNotFoundException("Test con id " + testId + " no encontrado"));

    entity.setFecha(dto.getFecha());
    entity.setTipo(dto.getTipo());
    entity.setResultado(dto.getResultado());
    entity.setLote(dto.getLote());

    final var response = testRepository.save(entity);
    return testMapper.toDto(response);
  }

  public void removeTest(Integer testId) {
    log.debug("Eliminar test: {}", testId);

    final var entity = testRepository.findById(testId).orElseThrow(() ->
        new ResourceNotFoundException("Test con id " + testId + " no encontrado"));
    testRepository.delete(entity);
  }
}