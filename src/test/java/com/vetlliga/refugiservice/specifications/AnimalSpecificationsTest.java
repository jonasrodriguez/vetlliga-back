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
 *   <li><b>Deworming</b> (interna/externa): included when the LATEST deworming of that tipo is on
 *       or after the selected date ({@code MAX(fecha of tipo) >= selected}). Never-dewormed animals
 *       are EXCLUDED. Same semantics as vacuna/test, but scoped per deworming tipo.
 *   <li><b>Vacuna / Test</b>: {@code MAX(fecha) >= selected}; never-vaccinated/tested are EXCLUDED.
 *   <li>Every query also carries the always-on {@code activo = true} predicate.
 * </ul>
 */
@DataJpaTest
class AnimalSpecificationsTest {

  @Autowired private TestEntityManager em;
  @Autowired private AnimalRepository animalRepository;

  // Fixed reference date so tests are deterministic. It is used directly as the selected filter date.
  private static final LocalDate HOY = LocalDate.of(2024, 6, 15);

  // ===================== DEWORMING — INTERNA =====================

  @Test
  void desp_interna_includesOnOrAfterSelected_excludesBeforeAndNever() {
    Animal onDate = withDeworming("onDate", INTERNA, HOY); // == selected -> included (>=)
    Animal after = withDeworming("after", INTERNA, HOY.plusDays(10)); // included
    Animal latestAfter = animal("latestAfter"); // old + recent, MAX >= selected -> included
    deworm(latestAfter, INTERNA, HOY.minusMonths(2));
    deworm(latestAfter, INTERNA, HOY.plusDays(1));

    withDeworming("before", INTERNA, HOY.minusDays(1)); // latest before selected -> excluded
    animal("never"); // no deworming -> excluded (NULL >= fecha is false)
    withDeworming("onlyExterna", EXTERNA, HOY.plusDays(1)); // no interna record -> excluded
    Animal inactiveAfter = withDeworming("inactiveAfter", INTERNA, HOY.plusDays(1));
    inactiveAfter.setActivo(false); // matches date but inactive -> excluded

    assertThat(ids(internaOn(HOY)))
        .containsExactlyInAnyOrder(onDate.getId(), after.getId(), latestAfter.getId());
  }

  @Test
  void desp_interna_boundaryIsInclusiveOnSelectedDate() {
    Animal onDate = withDeworming("onDate", INTERNA, HOY); // == selected -> included (>= inclusive)
    withDeworming("dayBefore", INTERNA, HOY.minusDays(1)); // excluded

    assertThat(ids(internaOn(HOY))).containsExactly(onDate.getId());
  }

  @Test
  void desp_interna_usesLatestDeworming() {
    // Latest record decides: an old + a recent record is included because MAX >= selected;
    // several records all before the selected date are excluded.
    Animal latestQualifies = animal("latestQualifies");
    deworm(latestQualifies, INTERNA, LocalDate.of(2024, 3, 1)); // before
    deworm(latestQualifies, INTERNA, HOY.plusDays(1)); // after -> MAX qualifies

    Animal allBefore = animal("allBefore");
    deworm(allBefore, INTERNA, LocalDate.of(2024, 5, 1));
    deworm(allBefore, INTERNA, LocalDate.of(2024, 6, 1)); // MAX still before selected

    assertThat(ids(internaOn(HOY))).containsExactly(latestQualifies.getId());
  }

  @Test
  void desp_interna_ignoresExternaHistory() {
    // Filtering interna only looks at interna records: a recent externa does not qualify an animal
    // whose interna is absent, and an animal with a qualifying interna is included.
    Animal internaRecent = withDeworming("internaRecent", INTERNA, HOY);
    withDeworming("externaOnlyRecent", EXTERNA, HOY.plusDays(1)); // no interna -> excluded

    assertThat(ids(internaOn(HOY))).containsExactly(internaRecent.getId());
  }

  // ===================== DEWORMING — EXTERNA =====================

  @Test
  void desp_externa_includesOnOrAfterSelected_excludesBeforeAndNever() {
    Animal onDate = withDeworming("onDate", EXTERNA, HOY); // included
    Animal after = withDeworming("after", EXTERNA, HOY.plusDays(10)); // included

    withDeworming("before", EXTERNA, HOY.minusDays(1)); // excluded
    animal("never"); // excluded
    withDeworming("onlyInterna", INTERNA, HOY.plusDays(1)); // no externa record -> excluded

    assertThat(ids(externaOn(HOY))).containsExactlyInAnyOrder(onDate.getId(), after.getId());
  }

  @Test
  void desp_externa_dewormedShortlyAfterFilterDate_appears() {
    // Reported case: filtering externa on Jul 13 must return a dog last dewormed externally on Jul 17.
    Animal perro = withDeworming("Firulais", EXTERNA, LocalDate.of(2024, 7, 17));

    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setDesparasitoExterna(LocalDate.of(2024, 7, 13));

    assertThat(ids(c)).containsExactly(perro.getId());
  }

  // ===================== DEWORMING — COMBINED =====================

  @Test
  void desp_combined_internaAndExterna_bothMustMatch() {
    Animal bothMatch = animal("bothMatch");
    deworm(bothMatch, INTERNA, HOY);
    deworm(bothMatch, EXTERNA, HOY.plusDays(2)); // both >= selected -> included

    Animal internaMatchExternaBefore = animal("internaMatchExternaBefore");
    deworm(internaMatchExternaBefore, INTERNA, HOY); // matches
    deworm(internaMatchExternaBefore, EXTERNA, HOY.minusDays(1)); // externa fails -> excluded

    animal("neither"); // no dewormings -> excluded on both

    ListadoAnimalesCriteria c = new ListadoAnimalesCriteria();
    c.setDesparasitoInterna(HOY);
    c.setDesparasitoExterna(HOY);

    assertThat(ids(c)).containsExactly(bothMatch.getId());
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
