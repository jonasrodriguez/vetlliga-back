package com.vetlliga.refugiservice.constants;

import lombok.Getter;

@Getter
public enum LocalizacionGato {
  HOSPITALIZACION(0),
  HOSPITALIZACION_CONSULTA(1),
  CUARENTENA_ENTRADA(2),
  CUARENTENA_SALIDA(3),
  ADAPTACION(4),
  CHIQUIPARK(5),
  PATIO_VERDE(6),
  ZONA_LEUCEMIA(7),
  ANTIGUA_ADAPTACION(8),
  COLONIA(9),
  COLONIA_EXTERNA(10),
  PROPIETARIO(11);

  private final int value;

  LocalizacionGato(int value) {
    this.value = value;
  }

  public static LocalizacionGato fromValue(int value) {
    for (LocalizacionGato loc : LocalizacionGato.values()) {
      if (loc.getValue() == value) {
        return loc;
      }
    }
    throw new IllegalArgumentException("Invalid LocalizacionGato value: " + value);
  }
}
