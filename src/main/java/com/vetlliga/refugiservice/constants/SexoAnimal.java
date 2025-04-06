package com.vetlliga.refugiservice.constants;

import lombok.Getter;

@Getter
public enum SexoAnimal {
  MACHO("M"),
  HEMBRA("H"),
  NO_DEFINIDO("N");

  private final String value;

  SexoAnimal(String value) {
    this.value = value;
  }

  public static SexoAnimal fromValue(String value) {
    for (SexoAnimal sexo : SexoAnimal.values()) {
      if (sexo.getValue().equals(value)) {
        return sexo;
      }
    }
    throw new IllegalArgumentException("Invalid SexoAnimal value: " + value);
  }
}
