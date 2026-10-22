package com.vetlliga.refugiservice.services;

import static com.vetlliga.refugiservice.constants.TipoAnimal.GATO;
import static com.vetlliga.refugiservice.constants.TipoAnimal.PERRO;
import static com.vetlliga.refugiservice.constants.TipoDesparasitacion.EXTERNA;
import static com.vetlliga.refugiservice.constants.TipoDesparasitacion.INTERNA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.dtos.RecordatorioDto;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordatorioServiceTest {

  @Mock private AnimalRepository animalRepository;
  @InjectMocks private RecordatorioService service;

  @Captor private ArgumentCaptor<LocalDateTime> limitePerroCaptor;
  @Captor private ArgumentCaptor<LocalDateTime> limiteGatoCaptor;

  @Test
  void interna_usesThreeMonthsForDogsAndSixForCats() {
    when(animalRepository.findPendientesDesparasitacion(any(), any(), any())).thenReturn(List.of());

    service.getRecordatorios(INTERNA);

    verify(animalRepository)
        .findPendientesDesparasitacion(eq(INTERNA), limitePerroCaptor.capture(), limiteGatoCaptor.capture());
    assertThat(limitePerroCaptor.getValue())
        .isCloseTo(LocalDateTime.now().minusMonths(3), within(5, ChronoUnit.SECONDS));
    assertThat(limiteGatoCaptor.getValue())
        .isCloseTo(LocalDateTime.now().minusMonths(6), within(5, ChronoUnit.SECONDS));
  }

  @Test
  void externa_usesOneMonthForDogsAndSixForCats() {
    when(animalRepository.findPendientesDesparasitacion(any(), any(), any())).thenReturn(List.of());

    service.getRecordatorios(EXTERNA);

    verify(animalRepository)
        .findPendientesDesparasitacion(eq(EXTERNA), limitePerroCaptor.capture(), limiteGatoCaptor.capture());
    assertThat(limitePerroCaptor.getValue())
        .isCloseTo(LocalDateTime.now().minusMonths(1), within(5, ChronoUnit.SECONDS));
    assertThat(limiteGatoCaptor.getValue())
        .isCloseTo(LocalDateTime.now().minusMonths(6), within(5, ChronoUnit.SECONDS));
  }

  @Test
  void mapsRowToDtoAndStampsQueriedType() {
    LocalDateTime fecha = LocalDateTime.of(2025, 1, 10, 12, 0);
    when(animalRepository.findPendientesDesparasitacion(any(), any(), any()))
        .thenReturn(List.<Object[]>of(row(7, PERRO, "Rex", 12.5f, fecha)));

    List<RecordatorioDto> result = service.getRecordatorios(INTERNA);

    assertThat(result)
        .singleElement()
        .satisfies(
            dto -> {
              assertThat(dto.getId()).isEqualTo(7);
              assertThat(dto.getTipo()).isEqualTo(PERRO);
              assertThat(dto.getNombre()).isEqualTo("Rex");
              assertThat(dto.getPeso()).isEqualTo(12.5f);
              assertThat(dto.getTipoDesparasitacion()).isEqualTo(INTERNA); // not in the row; added by the service
              assertThat(dto.getFechaDesparasitacion()).isEqualTo(LocalDate.of(2025, 1, 10)); // LocalDateTime -> LocalDate
            });
  }

  @Test
  void mapsNullWeightAndNullLastDeworming() {
    when(animalRepository.findPendientesDesparasitacion(any(), any(), any()))
        .thenReturn(List.<Object[]>of(row(7, GATO, "Misi", null, null)));

    RecordatorioDto dto = service.getRecordatorios(INTERNA).getFirst();

    assertThat(dto.getPeso()).isNull();
    assertThat(dto.getFechaDesparasitacion()).isNull();
  }

  @Test
  void mapsEveryRowPreservingOrder() {
    when(animalRepository.findPendientesDesparasitacion(any(), any(), any()))
        .thenReturn(
            List.<Object[]>of(
                row(1, PERRO, "Rex", 12f, LocalDateTime.now().minusMonths(4)),
                row(2, GATO, "Misi", null, null)));

    assertThat(service.getRecordatorios(INTERNA))
        .extracting(RecordatorioDto::getId)
        .containsExactly(1, 2);
  }

  /** Mirrors the query projection: SELECT a.id, a.tipo, a.nombre, p.peso, MAX(d.fecha). */
  private static Object[] row(
      Integer id, TipoAnimal tipo, String nombre, Float peso, LocalDateTime ultimaDesparasitacion) {
    return new Object[] {id, tipo, nombre, peso, ultimaDesparasitacion};
  }
}
