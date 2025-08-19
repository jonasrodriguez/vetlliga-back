package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.DocumentoDto;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Documento;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentosMapper {

  DocumentoDto toDto(Documento entity);

  @Mapping(target = "animal", ignore = true)
  Documento toEntity(DocumentoDto dto, @Context Animal animal);

  @AfterMapping
  default void setAnimal(@MappingTarget Documento entity, @Context Animal animal) {
    entity.setAnimal(animal);
  }
}
