package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.HistorialDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Historial;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HistorialMapper {

  HistorialDto toDto(Historial entity);

  Historial toEntity(HistorialDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Historial entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
