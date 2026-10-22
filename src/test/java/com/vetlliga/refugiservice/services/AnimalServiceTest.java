package com.vetlliga.refugiservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vetlliga.refugiservice.dtos.AnimalDto;
import com.vetlliga.refugiservice.dtos.ListadoAnimalesCriteria;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.AnimalMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class AnimalServiceTest {

  @Mock
  private AnimalRepository animalRepository;
  @Mock
  private AnimalMapper animalMapper;
  @InjectMocks
  private AnimalService animalService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void listAnimals_mapsEachEntityToDto() {
    Animal entity = new Animal();
    AnimalDto dto = AnimalDto.builder().id(1).build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<Animal> page = new PageImpl<>(List.of(entity));
    when(animalRepository.findAll(ArgumentMatchers.<Specification<Animal>>any(), any(Pageable.class)))
        .thenReturn(page);
    when(animalMapper.toDto(entity)).thenReturn(dto);

    Page<AnimalDto> result = animalService.listAnimals(new ListadoAnimalesCriteria(), pageable);

    assertEquals(List.of(dto), result.getContent());
  }

  @Test
  void getAnimal_returnsMappedDto() {
    Animal entity = new Animal();
    AnimalDto dto = AnimalDto.builder().id(1).build();
    when(animalRepository.findById(1)).thenReturn(Optional.of(entity));
    when(animalMapper.toDto(entity)).thenReturn(dto);

    assertSame(dto, animalService.getAnimal(1));
  }

  @Test
  void getAnimal_notFound_throwsNoSuchElement() {
    when(animalRepository.findById(99)).thenReturn(Optional.empty());

    // NOTE: getAnimal uses bare orElseThrow() -> NoSuchElementException, unlike the other
    // methods which throw ResourceNotFoundException. Flagged as an inconsistency.
    assertThrows(NoSuchElementException.class, () -> animalService.getAnimal(99));
  }

  @Test
  void altaAnimal_resetsIdAndStampsCreationMetadata() {
    AnimalDto input = AnimalDto.builder().id(123).build();
    Animal entity = new Animal();
    entity.setId(123); // mapper carries the id across; the service must null it before saving
    when(animalMapper.toEntity(input)).thenReturn(entity);
    when(animalRepository.save(any(Animal.class))).thenReturn(entity);
    when(animalMapper.toDto(entity)).thenReturn(input);

    animalService.altaAnimal(input, "alice");

    ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
    verify(animalRepository).save(captor.capture());
    Animal saved = captor.getValue();
    assertNull(saved.getId());
    assertEquals("alice", saved.getUsuarioCreacion());
    assertEquals(LocalDate.now(), saved.getFechaCreacion());
  }

  @Test
  void actualizarAnimal_updatesExistingAndStampsModificationMetadata() {
    AnimalDto input = AnimalDto.builder().id(1).build();
    Animal existing = new Animal();
    existing.setId(1);
    when(animalRepository.findById(1)).thenReturn(Optional.of(existing));
    when(animalRepository.save(existing)).thenReturn(existing);
    when(animalMapper.toDto(existing)).thenReturn(input);

    animalService.actualizarAnimal(1, input, "bob");

    verify(animalMapper).updateEntityFromDto(input, existing);
    verify(animalRepository).save(existing);
    assertEquals("bob", existing.getUsuarioModificacion());
    assertEquals(LocalDate.now(), existing.getFechaModificacion());
  }

  @Test
  void actualizarAnimal_notFound_throwsResourceNotFound() {
    when(animalRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> animalService.actualizarAnimal(99, AnimalDto.builder().build(), "bob"));
    verify(animalRepository, never()).save(any());
  }

  @Test
  void borradoAnimal_existing_deletes() {
    when(animalRepository.existsById(1)).thenReturn(true);

    animalService.borradoAnimal(1);

    verify(animalRepository).deleteById(1);
  }

  @Test
  void borradoAnimal_notFound_throwsAndDoesNotDelete() {
    when(animalRepository.existsById(99)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> animalService.borradoAnimal(99));
    verify(animalRepository, never()).deleteById(any());
  }

  @Test
  void toggleActive_updatesFlagAndClearsLocalizacion() {
    Animal existing = new Animal();
    existing.setActivo(true);
    when(animalRepository.findById(1)).thenReturn(Optional.of(existing));

    animalService.toggleActive(1, false);

    verify(animalRepository).save(existing);
    assertFalse(existing.getActivo());
    assertNull(existing.getLocalizacion());
  }

  @Test
  void toggleActive_notFound_throwsResourceNotFound() {
    when(animalRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> animalService.toggleActive(99, true));
  }
}
