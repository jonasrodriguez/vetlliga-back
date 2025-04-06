package com.vetlliga.refugiservice.constants;

import lombok.Getter;

@Getter
public enum TipoAnimal {
  PERRO("P"),
  GATO("G");

  private final String value;

  TipoAnimal(String value) {
    this.value = value;
  }

  public static TipoAnimal fromValue(String value) {
    for (TipoAnimal tipo : TipoAnimal.values()) {
      if (tipo.getValue().equals(value)) {
        return tipo;
      }
    }
    throw new IllegalArgumentException("Invalid TipoAnimal value: " + value);
  }
}
