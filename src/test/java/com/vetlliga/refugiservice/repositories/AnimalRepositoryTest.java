package com.vetlliga.refugiservice.repositories;

import static com.vetlliga.refugiservice.constants.TipoAnimal.GATO;
import static com.vetlliga.refugiservice.constants.TipoAnimal.PERRO;
import static com.vetlliga.refugiservice.constants.TipoDesparasitacion.EXTERNA;
import static com.vetlliga.refugiservice.constants.TipoDesparasitacion.INTERNA;
import static org.assertj.core.api.Assertions.assertThat;

import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Desparasitacion;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Verifies that {@code findPendientesDesparasitacion} returns EXACTLY the animals that are due
 * (never dewormed for the queried type, or last dewormed before their window), where the window
 * depends on BOTH the animal type and the deworming type:
 *
 * <pre>
 *            EXTERNA   INTERNA
 *   PERRO    1 month   3 months
 *   GATO     6 months  6 months
 * </pre>
 *
 * Each test seeds several animals at once and asserts the full returned id-set, so an animal
 * leaking in (or wrongly dropping out) fails the test.
 */
@DataJpaTest
class AnimalRepositoryTest {

  @Autowired private TestEntityManager em;
  @Autowired private AnimalRepository animalRepository;

  private final LocalDateTime ahora = LocalDateTime.now();

  // Window limits, derived the same way the service derives them.
  private final LocalDateTime internaPerro = ahora.minusMonths(3);
  private final LocalDateTime internaGato = ahora.minusMonths(6);
  private final LocalDateTime externaPerro = ahora.minusMonths(1);
  private final LocalDateTime externaGato = ahora.minusMonths(6);

  @Test
  void interna_returnsOnlyDueOrNeverDewormed() {
    Animal dogNever = animal(PERRO, "dogNever");
    Animal dogRecent = dewormed(PERRO, "dogRecent", INTERNA, ahora.minusMonths(2)); // < 3mo window
    Animal dogDue = dewormed(PERRO, "dogDue", INTERNA, ahora.minusMonths(5)); // > 3mo window
    Animal catNever = animal(GATO, "catNever");
    Animal catRecent = dewormed(GATO, "catRecent", INTERNA, ahora.minusMonths(5)); // < 6mo window
    Animal catDue = dewormed(GATO, "catDue", INTERNA, ahora.minusMonths(8)); // > 6mo window
    Animal dogOnlyExterna = dewormed(PERRO, "dogOnlyExterna", EXTERNA, ahora.minusDays(1)); // never INTERNA
    Animal inactive = inactive(PERRO, "inactive"); // excluded regardless

    // silence "unused" without weakening intent: these must NOT appear
    assertThat(Set.of(dogRecent, catRecent, inactive)).isNotEmpty();

    assertThat(idsReturned(INTERNA, internaPerro, internaGato))
        .containsExactlyInAnyOrder(
            dogNever.getId(), dogDue.getId(), catNever.getId(), catDue.getId(), dogOnlyExterna.getId());
  }

  @Test
  void externa_returnsOnlyDueOrNeverDewormed() {
    Animal dogNever = animal(PERRO, "dogNever");
    Animal dogRecent = dewormed(PERRO, "dogRecent", EXTERNA, ahora.minusDays(20)); // < 1mo window
    Animal dogDue = dewormed(PERRO, "dogDue", EXTERNA, ahora.minusMonths(2)); // > 1mo window
    Animal catNever = animal(GATO, "catNever");
    Animal catRecent = dewormed(GATO, "catRecent", EXTERNA, ahora.minusMonths(4)); // < 6mo window
    Animal catDue = dewormed(GATO, "catDue", EXTERNA, ahora.minusMonths(8)); // > 6mo window
    Animal dogOnlyInterna = dewormed(PERRO, "dogOnlyInterna", INTERNA, ahora.minusDays(1)); // never EXTERNA
    Animal inactive = inactive(PERRO, "inactive");

    assertThat(Set.of(dogRecent, catRecent, inactive)).isNotEmpty();

    assertThat(idsReturned(EXTERNA, externaPerro, externaGato))
        .containsExactlyInAnyOrder(
            dogNever.getId(), dogDue.getId(), catNever.getId(), catDue.getId(), dogOnlyInterna.getId());
  }

  @Test
  void sameAge_windowDependsOnAnimalType_interna() {
    // Both dewormed 4 months ago: dog (3mo window) is due, cat (6mo window) is not.
    Animal perro = dewormed(PERRO, "Rex", INTERNA, ahora.minusMonths(4));
    Animal gato = dewormed(GATO, "Misi", INTERNA, ahora.minusMonths(4));

    assertThat(gato).isNotNull();
    assertThat(idsReturned(INTERNA, internaPerro, internaGato)).containsExactly(perro.getId());
  }

  @Test
  void sameAge_windowDependsOnAnimalType_externa() {
    // Both dewormed 2 months ago: dog (1mo window) is due, cat (6mo window) is not.
    Animal perro = dewormed(PERRO, "Rex", EXTERNA, ahora.minusMonths(2));
    Animal gato = dewormed(GATO, "Misi", EXTERNA, ahora.minusMonths(2));

    assertThat(gato).isNotNull();
    assertThat(idsReturned(EXTERNA, externaPerro, externaGato)).containsExactly(perro.getId());
  }

  @Test
  void dewormingTypeIsIndependent() {
    // A dog dewormed externally yesterday is still internally overdue (never dewormed internally),
    // and vice-versa.
    Animal externaOnly = dewormed(PERRO, "externaOnly", EXTERNA, ahora.minusDays(1));
    Animal internaOnly = dewormed(PERRO, "internaOnly", INTERNA, ahora.minusDays(1));

    assertThat(idsReturned(INTERNA, internaPerro, internaGato)).containsExactly(externaOnly.getId());
    assertThat(idsReturned(EXTERNA, externaPerro, externaGato)).containsExactly(internaOnly.getId());
  }

  @Test
  void boundary_dueOnlyWhenOlderThanWindow() {
    // Window edge (HAVING "< limit"): just past the limit is due, just inside is not.
    // We don't test "exactly at the limit" because sub-second precision is truncated on persist,
    // so an exact-instant compare isn't reproducible across databases.
    Animal justOutside = dewormed(PERRO, "justOutside", INTERNA, internaPerro.minusSeconds(1)); // > 3mo -> due
    Animal justInside = dewormed(PERRO, "justInside", INTERNA, internaPerro.plusSeconds(1)); // < 3mo -> not due

    assertThat(justInside).isNotNull();
    assertThat(idsReturned(INTERNA, internaPerro, internaGato)).containsExactly(justOutside.getId());
  }

  // ---- helpers ----

  private Set<Integer> idsReturned(TipoDesparasitacion tipo, LocalDateTime limitePerro, LocalDateTime limiteGato) {
    em.flush();
    em.clear();
    return animalRepository.findPendientesDesparasitacion(tipo, limitePerro, limiteGato).stream()
        .map(row -> (Integer) row[0])
        .collect(Collectors.toSet());
  }

  private Animal animal(TipoAnimal tipo, String nombre) {
    Animal a = new Animal();
    a.setTipo(tipo);
    a.setNombre(nombre);
    a.setActivo(true);
    a.setEstado(EstadoAnimal.EN_PROTECTORA);
    return em.persist(a);
  }

  private Animal inactive(TipoAnimal tipo, String nombre) {
    Animal a = animal(tipo, nombre);
    a.setActivo(false);
    return em.persist(a);
  }

  private Animal dewormed(TipoAnimal tipo, String nombre, TipoDesparasitacion dtype, LocalDateTime fecha) {
    Animal a = animal(tipo, nombre);
    Desparasitacion d = new Desparasitacion();
    d.setAnimal(a);
    d.setTipo(dtype);
    d.setFecha(fecha);
    em.persist(d);
    return a;
  }
}
