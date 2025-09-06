package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.UsuarioDto;
import com.vetlliga.refugiservice.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

  @Mapping(target = "password", ignore = true)
  UsuarioDto toDto(Usuario entity);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "hidden", ignore = true)
  Usuario toEntity(UsuarioDto dto);
}
