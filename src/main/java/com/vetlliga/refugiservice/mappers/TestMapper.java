package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.TestDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Intervencion;
import com.vetlliga.refugiservice.entities.Test;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TestMapper {

  TestDto toDto(Test entity);

  Test toEntity(TestDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Test entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
