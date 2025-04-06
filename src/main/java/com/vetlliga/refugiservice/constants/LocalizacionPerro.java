package com.vetlliga.refugiservice.constants;

import lombok.Getter;

@Getter
public enum LocalizacionPerro {
  NIVEL_1(0),
  NIVEL_2_3(1);

  private final int value;

  LocalizacionPerro(int value) {
    this.value = value;
  }

  public static LocalizacionPerro fromValue(int value) {
    for (LocalizacionPerro loc : LocalizacionPerro.values()) {
      if (loc.getValue() == value) {
        return loc;
      }
    }
    throw new IllegalArgumentException("Invalid LocalizacionPerro value: " + value);
  }
}
