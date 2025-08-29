package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.entities.Localizacion;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalizacionCache {

  private final LocalizacionRepository repository;
  private final Map<TipoAnimal, Map<Integer, Localizacion>> cache = new EnumMap<>(TipoAnimal.class);

  @PostConstruct
  public void init() {
    refresh();
  }

  public synchronized void refresh() {
    cache.clear();
    for (TipoAnimal tipo : TipoAnimal.values()) {
      var locs = repository.findAll().stream().filter(l -> l.getTipo() == tipo).toList();
      Map<Integer, Localizacion> map = locs.stream()
          .collect(Collectors.toMap(Localizacion::getId, Function.identity()));

      cache.put(tipo, map);
    }
  }

  public Localizacion get(TipoAnimal tipo, Integer id) {
    return cache.getOrDefault(tipo, Map.of()).get(id);
  }

  public Map<Integer, Localizacion> getAllByTipo(TipoAnimal tipo) {
    return cache.getOrDefault(tipo, Map.of());
  }

  public Localizacion getById(Integer id) {
    for (Map<Integer, Localizacion> map : cache.values()) {
      if (map.containsKey(id)) {
        return map.get(id);
      }
    }
    return null;
  }
}

