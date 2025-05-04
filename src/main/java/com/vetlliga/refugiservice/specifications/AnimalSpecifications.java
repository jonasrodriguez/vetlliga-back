package com.vetlliga.refugiservice.specifications;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.LocalizacionGato;
import com.vetlliga.refugiservice.constants.LocalizacionPerro;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.dtos.ListadoAnimalesCriteria;
import com.vetlliga.refugiservice.entities.Animal;
import com.vetlliga.refugiservice.entities.Desparasitacion;
import com.vetlliga.refugiservice.entities.Test;
import com.vetlliga.refugiservice.entities.Vacunacion;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class AnimalSpecifications {

  public static Specification<Animal> filterByCriteria(ListadoAnimalesCriteria criteria) {
    return (root, query, builder) -> {
      if (isNull(criteria)) return builder.conjunction();

      List<Predicate> predicates = new ArrayList<>();

      addTipo(criteria, root, builder, predicates);
      addEstado(criteria, root, builder, predicates);
      addLocalizacion(criteria, root, builder, predicates);
      addFechaEstado(criteria, root, builder, predicates);
      addFechaLocalizacion(criteria, root, builder, predicates);
      addUltimaVacuna(criteria, root, query, builder, predicates);
      addUltimaParasito(criteria, root, query, builder, predicates);
      addUltimoTest(criteria, root, query, builder, predicates);

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static void addTipo(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaBuilder builder, List<Predicate> predicates) {

    if (nonNull(criteria.getTipo())) {
      TipoAnimal tipo = TipoAnimal.fromValue(criteria.getTipo());
      predicates.add(builder.equal(root.get("tipo"), tipo.name()));
    }
  }

  private static void addEstado(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaBuilder builder, List<Predicate> predicates) {

    if (nonNull(criteria.getEstado())) {
      EstadoAnimal estado = EstadoAnimal.fromValue(criteria.getEstado());
      predicates.add(builder.equal(root.get("estado"), estado.name()));
    }
  }

  private static void addLocalizacion(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaBuilder builder, List<Predicate> predicates) {

    if (nonNull(criteria.getLocalizacion()) && nonNull(criteria.getTipo())) {
      TipoAnimal tipo = TipoAnimal.fromValue(criteria.getTipo());
      if (tipo == TipoAnimal.PERRO) {
        predicates.add(
            builder.equal(root.get("localizacionPerro"), LocalizacionPerro.fromValue(criteria.getLocalizacion()))
        );
      } else if (tipo == TipoAnimal.GATO) {
        predicates.add(
            builder.equal(root.get("localizacionGato"), LocalizacionGato.fromValue(criteria.getLocalizacion()))
        );
      }
    }
  }

  private static void addFechaEstado(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaBuilder builder, List<Predicate> predicates) {
    if (nonNull(criteria.getFechaEstado())) {

      Expression<Integer> month = builder.function("MONTH", Integer.class, root.get("fechaEstado"));
      Expression<Integer> year = builder.function("YEAR", Integer.class, root.get("fechaEstado"));
      predicates.add(builder.equal(month, criteria.getFechaEstado().getMonthValue()));
      predicates.add(builder.equal(year, criteria.getFechaEstado().getYear()));
    }
  }

  private static void addFechaLocalizacion(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaBuilder builder, List<Predicate> predicates) {
    if (nonNull(criteria.getFechaLocalizacion())) {

      Expression<Integer> month = builder.function("MONTH", Integer.class, root.get("fechaLocalizacion"));
      Expression<Integer> year = builder.function("YEAR", Integer.class, root.get("fechaLocalizacion"));
      predicates.add(builder.equal(month, criteria.getFechaLocalizacion().getMonthValue()));
      predicates.add(builder.equal(year, criteria.getFechaLocalizacion().getYear()));
    }
  }

  private static void addUltimaVacuna(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaQuery<?> query, CriteriaBuilder builder,
      List<Predicate> predicates) {

    if (nonNull(criteria.getUltimaVacunaDesde()) || nonNull(criteria.getUltimaVacunaHasta())) {
      Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
      Root<Vacunacion> subRoot = subquery.from(Vacunacion.class);
      subquery.select(builder.greatest(subRoot.<LocalDate>get("fecha")))
          .where(builder.equal(subRoot.get("animal"), root));

      if (nonNull(criteria.getUltimaVacunaDesde())) {
        predicates.add(builder.greaterThanOrEqualTo(subquery, criteria.getUltimaVacunaDesde()));
      }
      if (nonNull(criteria.getUltimaVacunaHasta())) {
        predicates.add(builder.lessThanOrEqualTo(subquery, criteria.getUltimaVacunaHasta()));
      }
    }
  }

  private static void addUltimaParasito(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaQuery<?> query, CriteriaBuilder builder,
      List<Predicate> predicates) {

    if (nonNull(criteria.getUltimaParasitoDesde()) || nonNull(criteria.getUltimaParasitoHasta())) {
      Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
      Root<Desparasitacion> subRoot = subquery.from(Desparasitacion.class);
      subquery.select(builder.greatest(subRoot.<LocalDate>get("fecha")))
          .where(builder.equal(subRoot.get("animal"), root));

      if (nonNull(criteria.getUltimaParasitoDesde())) {
        predicates.add(builder.greaterThanOrEqualTo(subquery, criteria.getUltimaParasitoDesde()));
      }
      if (nonNull(criteria.getUltimaParasitoHasta())) {
        predicates.add(builder.lessThanOrEqualTo(subquery, criteria.getUltimaParasitoHasta()));
      }
    }
  }

  private static void addUltimoTest(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaQuery<?> query, CriteriaBuilder builder,
      List<Predicate> predicates) {

    if (nonNull(criteria.getUltimoTestDesde()) || nonNull(criteria.getUltimoTestHasta())) {
      Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
      Root<Test> subRoot = subquery.from(Test.class);
      subquery.select(builder.greatest(subRoot.<LocalDate>get("fecha")))
          .where(builder.equal(subRoot.get("animal"), root));

      if (nonNull(criteria.getUltimoTestDesde())) {
        predicates.add(builder.greaterThanOrEqualTo(subquery, criteria.getUltimoTestDesde()));
      }
      if (nonNull(criteria.getUltimoTestHasta())) {
        predicates.add(builder.lessThanOrEqualTo(subquery, criteria.getUltimoTestHasta()));
      }
    }
  }
}