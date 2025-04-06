package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.IntervencionDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Intervencion;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IntervencionMapper {

  IntervencionDto toDto(Intervencion entity);

  Intervencion toEntity(IntervencionDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Intervencion entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
