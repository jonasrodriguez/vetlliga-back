package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.PesoDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Peso;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PesoMapper {

  PesoDto toDto(Peso entity);

  @Mapping(target = "animal", ignore = true)
  Peso toEntity(PesoDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Peso entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
