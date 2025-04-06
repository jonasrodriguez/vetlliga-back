package com.vetlliga.refugiservice.constants;

import lombok.Getter;

@Getter
public enum EstadoAnimal {
  EN_PROTECTORA(0),
  EN_ACOGIDA(1),
  RESERVADO(2),
  ADOPTADO(3),
  FALLECIDO(4);

  private final int value;

  EstadoAnimal(int value) {
    this.value = value;
  }

  public static EstadoAnimal fromValue(int value) {
    for (EstadoAnimal estado : EstadoAnimal.values()) {
      if (estado.getValue() == value) {
        return estado;
      }
    }
    throw new IllegalArgumentException("Invalid EstadoAnimal value: " + value);
  }
}
