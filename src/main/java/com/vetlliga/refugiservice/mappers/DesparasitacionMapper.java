package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.DesparasitacionDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Desparasitacion;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DesparasitacionMapper {

  DesparasitacionDto toDto(Desparasitacion entity);

  Desparasitacion toEntity(DesparasitacionDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Desparasitacion entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
