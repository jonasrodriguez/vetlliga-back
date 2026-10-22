package com.vetlliga.refugiservice.specifications;

import static com.vetlliga.refugiservice.constants.EstadoAnimal.ADOPTADO;
import static com.vetlliga.refugiservice.constants.EstadoAnimal.EN_PROTECTORA;
import static com.vetlliga.refugiservice.constants.TipoAnimal.GATO;
import static com.vetlliga.refugiservice.constants.TipoAnimal.PERRO;
import static com.vetlliga.refugiservice.constants.TipoDesparasitacion.EXTERNA;
import static com.vetlliga.refugiservice.constants.TipoDesparasitacion.INTERNA;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
import com.vetlliga.refugiservice.dtos.ListadoAnimalesCriteria;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Desparasitacion;
import com.vetlliga.refugiservice.entities.Localizacion;
import com.vetlliga.refugiservice.entities.Vacunacion;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Persistence-level coverage of {@link AnimalSpecifications#filterByCriteria}.
 *
 * <p>Emphasis on the date-driven filters, especially deworming (the most used). Each test seeds
 * several animals and asserts the exact id-set, so a leak or a drop both fail.
 *
 * <p>Semantics under test:
 *
 * <ul>
 *   <li><b>Deworming</b> (interna/externa): due when {@code MAX(fecha of tipo) <= selected - 3mo},
 *       OR never dewormed for that tipo. Uses the LATEST deworming of the tipo.
 *   <li><b>Vacuna / Test</b>: {@code MAX(fecha) >= selected}, and never-vaccinated/tested are
 *       EXCLUDED (no null branch) — the opposite direction from deworming. Flagged intentionally.
 *   <li>Every query also carries the always-on {@code activo = true} predicate.
 * </ul>
 */
@DataJpaTest
class AnimalSpecificationsTest {

  @Autowired private TestEntityManager em;
  @Autowired private AnimalRepository animalRepository;

  // Fixed reference date so tests are deterministic.
  private static final LocalDate HOY = LocalDate.of(2024, 6, 15);
  private static final LocalDate LIMITE_DESP = HOY.minusMonths(3); // 2024-03-15: last deworming must be <= this

  // ===================== DEWORMING — INTERNA =====================

  @Test
  void interna_includesDueNeverAndOnlyExterna_excludesRecentMaxAndInactive() {
    Animal dueOld = withDeworming("dueOld", INTERNA, LocalDate.of(2024, 1, 1)); // before limit -> due
    Animal never = animal("never"); // no deworming -> due
    Animal onlyExterna = withDeworming("onlyExterna", EXTERNA, LocalDate.of(2024, 6, 1)); // never interna -> due

    withDeworming("recent", INTERNA, LocalDate.of(2024, 6, 1)); // after limit -> NOT due
    Animal maxRecent = animal("maxRecent"); // MAX(fecha) is the recent one -> NOT due
    deworm(maxRecent, INTERNA, LocalDate.of(2023, 12, 1));
    deworm(maxRecent, INTERNA, LocalDate.of(2024, 6, 1));
    Animal inactiveDue = withDeworming("inactiveDue", INTERNA, LocalDate.of(2024, 1, 1)); // due but inactive
    inactiveDue.setActivo(false);

    assertThat(ids(internaOn(HOY)))
        .containsExactlyInAnyOrder(dueOld.getId(), never.getId(), onlyExterna.getId());
  }

  @Test
  void interna_boundaryIsInclusiveOnTheLimit() {
    Animal before = withDeworming("before", INTERNA, LIMITE_DESP.minusDays(1)); // due
    Animal onLimit = withDeworming("onLimit", INTERNA, LIMITE_DESP); // due (<= is inclusive)
    withDeworming("after", INTERNA, LIMITE_DESP.plusDays(1)); // not due

    assertThat(ids(internaOn(HOY))).containsExactlyInAnyOrder(before.getId(), onLimit.getId());
  }

  @Test
  void interna_usesLatestDeworming() {
    Animal allOld = animal("allOld"); // MAX = 2024-02-01 -> due
    deworm(allOld, INTERNA, LocalDate.of(2023, 12, 1));
    deworm(allOld, INTERNA, LocalDate.of(2024, 2, 1));

    Animal latestRecent = animal("latestRecent"); // MAX = 2024-06-01 -> not due
    deworm(latestRecent, INTERNA, LocalDate.of(2024, 1, 1));
    deworm(latestRecent, INTERNA, LocalDate.of(2024, 6, 1));

    assertThat(ids(internaOn(HOY))).containsExactly(allOld.getId());
  }

  @Test
  void interna_ignoresExternaHistory() {
    // Interna due, but externa was done yesterday: filtering interna must still include it.
    Animal a = withDeworming("a", INTERNA, LocalDate.of(2024, 1, 1));
    deworm(a, EXTERNA, HOY.minusDays(1));

    assertThat(ids(internaOn(HOY))).containsExactly(a.getId());
  }

  // ===================== DEWORMING — EXTERNA =====================

  @Test
  void externa_includesDueAndNever_excludesRecent() {
    Animal dueOld = withDeworming("dueOld", EXTERNA, LocalDate.of(2024, 1, 1)); // due
    Animal never = animal("never"); // never externa -> due
    Animal onlyInterna = withDeworming("onlyInterna", INTERNA, LocalDate.of(2024, 1, 1)); // never externa -> due
    withDeworming("recent", EXTERNA, LocalDate.of(2024, 6, 1)); // not due

    assertThat(ids(externaOn(HOY)))
        .containsExactlyInAnyOrder(dueOld.getId(), never.getId(), onlyInterna.getId());
  }

  // ===================== DEWORMING — COMBINED =====================

  @Test
  void combined_internaAndExterna_bothMustBeDue() {
    Animal bothDue = animal("bothDue");
    deworm(bothDue, INTERNA, LocalDate.of(2024, 1, 1));
    deworm(bothDue, EXTERNA, LocalDate.of(2024, 1, 1));

    Animal internaDueExternaRecent = animal("internaDueExternaRecent");
    deworm(internaDueExternaRecent, INTERNA, LocalDate.of(2024, 1, 1)); // due
    deworm(internaDueExternaRecent, EXTERNA, LocalDate.of(2024, 6, 1)); // not due -> whole animal excluded

    Animal bothNever = animal("bothNever"); // null for both -> due on both

    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setDesparasitoInterna(HOY);
    c.setDesparasitoExterna(HOY);

    assertThat(ids(c)).containsExactlyInAnyOrder(bothDue.getId(), bothNever.getId());
  }

  // ===================== VACUNA (>= selected, excludes never) =====================

  @Test
  void vacuna_includesOnOrAfterSelected_excludesBeforeAndNever_usesLatest() {
    Animal onDate = animal("onDate");
    vaccinate(onDate, HOY); // == selected -> included (>=)
    Animal after = animal("after");
    vaccinate(after, HOY.plusDays(10)); // included
    Animal before = animal("before");
    vaccinate(before, HOY.minusDays(1)); // excluded
    Animal never = animal("never"); // no vacuna -> excluded (no null branch)
    Animal latestAfter = animal("latestAfter"); // MAX >= selected -> included
    vaccinate(latestAfter, HOY.minusMonths(2));
    vaccinate(latestAfter, HOY.plusDays(1));

    assertThat(never).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setVacuna(HOY);

    assertThat(ids(c)).containsExactlyInAnyOrder(onDate.getId(), after.getId(), latestAfter.getId());
  }

  // ===================== TEST (>= selected, excludes never) =====================

  @Test
  void test_includesOnOrAfterSelected_excludesBeforeAndNever() {
    Animal onDate = animal("onDate");
    prueba(onDate, HOY); // included
    Animal after = animal("after");
    prueba(after, HOY.plusDays(5)); // included
    Animal before = animal("before");
    prueba(before, HOY.minusDays(5)); // excluded
    Animal never = animal("never"); // excluded

    assertThat(never).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setTest(HOY);

    assertThat(ids(c)).containsExactlyInAnyOrder(onDate.getId(), after.getId());
  }

  // ===================== DATE: fechaEstado (MONTH + YEAR) =====================

  @Test
  void fechaEstado_matchesMonthAndYearIgnoringDay() {
    Animal match = animal("match");
    match.setFechaEstado(LocalDate.of(2024, 6, 30)); // same month+year, different day
    Animal otherMonth = animal("otherMonth");
    otherMonth.setFechaEstado(LocalDate.of(2024, 5, 15));
    Animal otherYear = animal("otherYear");
    otherYear.setFechaEstado(LocalDate.of(2023, 6, 15));

    assertThat(otherMonth).isNotNull();
    assertThat(otherYear).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setFechaEstado(LocalDate.of(2024, 6, 1));

    assertThat(ids(c)).containsExactly(match.getId());
  }

  @Test
  void fechaLocalizacion_matchesMonthAndYearIgnoringDay() {
    Animal match = animal("match");
    match.setFechaLocalizacion(LocalDate.of(2024, 6, 2));
    Animal otherMonth = animal("otherMonth");
    otherMonth.setFechaLocalizacion(LocalDate.of(2024, 7, 2));

    assertThat(otherMonth).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setFechaLocalizacion(LocalDate.of(2024, 6, 28));

    assertThat(ids(c)).containsExactly(match.getId());
  }

  // ===================== SEARCH =====================

  @Test
  void search_matchesAnyTextFieldCaseInsensitive() {
    Animal byNombre = animal("Rexy");
    Animal byChip = animal("chipOwner");
    byChip.setChip("CHIP-777");
    Animal byRegistro = animal("registroOwner");
    byRegistro.setNumeroRegistro("REG-123");
    Animal byEnfermedad = animal("sickOne");
    byEnfermedad.setEnfermedades("Leishmania");

    assertThat(ids(search("rexy"))).containsExactly(byNombre.getId());
    assertThat(ids(search("chip-777"))).containsExactly(byChip.getId());
    assertThat(ids(search("reg-123"))).containsExactly(byRegistro.getId());
    assertThat(ids(search("LEISH"))).containsExactly(byEnfermedad.getId()); // case-insensitive
  }

  @Test
  void search_blankIsIgnored() {
    Animal a = animal("a");
    Animal b = animal("b");

    assertThat(ids(search("   "))).containsExactlyInAnyOrder(a.getId(), b.getId());
  }

  // ===================== TIPO / ESTADO / LOCALIZACION =====================

  @Test
  void tipo_filtersBySpecies() {
    Animal perro = animal("perro"); // PERRO by default
    Animal gato = animal("gato");
    gato.setTipo(GATO);

    assertThat(perro).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setTipo(GATO.getValue()); // "G"

    assertThat(ids(c)).containsExactly(gato.getId());
  }

  @Test
  void estado_filtersByEstadoValue() {
    Animal enProtectora = animal("enProtectora"); // EN_PROTECTORA by default
    Animal adoptado = animal("adoptado");
    adoptado.setEstado(ADOPTADO);

    assertThat(enProtectora).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setEstado(ADOPTADO.getValue()); // 3

    assertThat(ids(c)).containsExactly(adoptado.getId());
  }

  @Test
  void localizacion_filtersById() {
    Localizacion jaula1 = localizacion("Jaula 1");
    Localizacion jaula2 = localizacion("Jaula 2");
    Animal a1 = animal("a1");
    a1.setLocalizacion(jaula1);
    Animal a2 = animal("a2");
    a2.setLocalizacion(jaula2);

    assertThat(a2).isNotNull();
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setLocalizacion(jaula1.getId());

    assertThat(ids(c)).containsExactly(a1.getId());
  }

  // ===================== ACTIVO (always on) / SORTING =====================

  @Test
  void noFilters_returnsOnlyActive() {
    Animal active1 = animal("active1");
    Animal active2 = animal("active2");
    Animal inactive = animal("inactive");
    inactive.setActivo(false);

    assertThat(inactive).isNotNull();
    assertThat(ids(new ListadoAnimalesCriteria()))
        .containsExactlyInAnyOrder(active1.getId(), active2.getId());
  }

  @Test
  void sorting_byNombreRespectsDirection() {
    Animal carlos = animal("Carlos");
    Animal ana = animal("Ana");
    Animal bruno = animal("Bruno");

    ListadoAnimalesCriteria asc = new ListadoAnimalesCriteria();
    asc.setSortBy("nombre");
    asc.setSortDirection("ASC");
    assertThat(idsOrdered(asc)).containsExactly(ana.getId(), bruno.getId(), carlos.getId());

    ListadoAnimalesCriteria desc = new ListadoAnimalesCriteria();
    desc.setSortBy("nombre");
    desc.setSortDirection("DESC");
    assertThat(idsOrdered(desc)).containsExactly(carlos.getId(), bruno.getId(), ana.getId());
  }

  // ---- query helpers ----

  private Set<Integer> ids(ListadoAnimalesCriteria criteria) {
    return result(criteria).stream().map(Animal::getId).collect(toSet());
  }

  private List<Integer> idsOrdered(ListadoAnimalesCriteria criteria) {
    return result(criteria).stream().map(Animal::getId).toList();
  }

  private List<Animal> result(ListadoAnimalesCriteria criteria) {
    em.flush();
    em.clear();
    return animalRepository.findAll(AnimalSpecifications.filterByCriteria(criteria));
  }

  private ListadoAnimalesCriteria internaOn(LocalDate fecha) {
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setDesparasitoInterna(fecha);
    return c;
  }

  private ListadoAnimalesCriteria externaOn(LocalDate fecha) {
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setDesparasitoExterna(fecha);
    return c;
  }

  private ListadoAnimalesCriteria search(String term) {
    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setSearch(term);
    return c;
  }

  // ---- fixture helpers ----

  private Animal animal(String nombre) {
    Animal a = new Animal();
    a.setNombre(nombre);
    a.setTipo(PERRO);
    a.setActivo(true);
    a.setEstado(EN_PROTECTORA);
    return em.persist(a);
  }

  private Animal withDeworming(String nombre, TipoDesparasitacion tipo, LocalDate fecha) {
    Animal a = animal(nombre);
    deworm(a, tipo, fecha);
    return a;
  }

  private void deworm(Animal a, TipoDesparasitacion tipo, LocalDate fecha) {
    Desparasitacion d = new Desparasitacion();
    d.setAnimal(a);
    d.setTipo(tipo);
    d.setFecha(fecha.atStartOfDay()); // entity field is LocalDateTime
    em.persist(d);
  }

  private void vaccinate(Animal a, LocalDate fecha) {
    Vacunacion v = new Vacunacion();
    v.setAnimal(a);
    v.setFecha(fecha.atStartOfDay());
    em.persist(v);
  }

  private void prueba(Animal a, LocalDate fecha) {
    com.vetlliga.refugiservice.entities.Test t = new com.vetlliga.refugiservice.entities.Test();
    t.setAnimal(a);
    t.setFecha(fecha.atStartOfDay());
    em.persist(t);
  }

  private Localizacion localizacion(String nombre) {
    Localizacion l = new Localizacion();
    l.setNombre(nombre);
    l.setTipo(PERRO);
    return em.persist(l);
  }
}
