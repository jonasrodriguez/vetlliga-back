package com.vetlliga.refugiservice.mappers;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;

class AnimalMapperTest {

  private DesparasitacionMapper desparasitacionMapper = mock(DesparasitacionMapper.class);
  private HistorialMapper historialMapper = mock(HistorialMapper.class);
  private IntervencionMapper intervencionMapper = mock(IntervencionMapper.class);
  private PesoMapper pesoMapper = mock(PesoMapper.class);
  private TestMapper testMapper = mock(TestMapper.class);
  private VacunacionMapper vacunacionMapper = mock(VacunacionMapper.class);

  private AnimalMapper mapper;

  @BeforeEach
  void setup() {
    mapper = new AnimalMapper(
        desparasitacionMapper,
        historialMapper,
        intervencionMapper,
        pesoMapper,
        testMapper,
        vacunacionMapper
    );
  }
}