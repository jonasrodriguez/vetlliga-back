package com.vetlliga.refugiservice.mappers;

import com.vetlliga.refugiservice.dtos.LocalizacionDto;
import com.vetlliga.refugiservice.entities.Localizacion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocalizacionMapper {

  LocalizacionDto toDto(Localizacion entity);

  Localizacion toEntity(LocalizacionDto dto);
}
