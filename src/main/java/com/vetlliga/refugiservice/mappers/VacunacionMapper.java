package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.VacunacionDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Vacunacion;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VacunacionMapper {

  VacunacionDto toDto(Vacunacion entity);

  Vacunacion toEntity(VacunacionDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Vacunacion entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
