package com.vetlliga.refugiservice.specifications;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.vetlliga.refugiservice.constants.EstadoAnimal;
import com.vetlliga.refugiservice.constants.LocalizacionGato;
import com.vetlliga.refugiservice.constants.LocalizacionPerro;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.constants.TipoDesparasitacion;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class AnimalSpecifications {

  public static Specification<Animal> filterByCriteria(ListadoAnimalesCriteria criteria) {
    return (root, query, builder) -> {
      if (isNull(criteria)) {
        return builder.conjunction();
      }

      List<Predicate> predicates = new ArrayList<>();

      addSearch(criteria, root, builder, predicates);
      addTipo(criteria, root, builder, predicates);
      addEstado(criteria, root, builder, predicates);
      addLocalizacion(criteria, root, builder, predicates);
      addFechaEstado(criteria, root, builder, predicates);
      addFechaLocalizacion(criteria, root, builder, predicates);
      addUltimaVacuna(criteria, root, query, builder, predicates);
      addUltimaDesparasitacion(criteria.getDesparasitoInternaDesde(), criteria.getDesparasitoInternaHasta(), TipoDesparasitacion.INTERNA, root, query, builder, predicates);
      addUltimaDesparasitacion(criteria.getDesparasitoExternaDesde(), criteria.getDesparasitoExternaHasta(), TipoDesparasitacion.EXTERNA, root, query, builder, predicates);
      addUltimoTest(criteria, root, query, builder, predicates);

      addSorting(criteria, query, builder, root);

      // Add "activo = true" filter
      predicates.add(builder.isTrue(root.get("activo")));

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static void addSearch(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaBuilder builder, List<Predicate> predicates) {

    if (nonNull(criteria.getSearch()) && !criteria.getSearch().isBlank()) {
      String likePattern = "%" + criteria.getSearch().toLowerCase() + "%";

      Predicate nombrePredicate = builder.like(builder.lower(root.get("nombre")), likePattern);
      Predicate chipPredicate = builder.like(builder.lower(root.get("chip")), likePattern);
      Predicate numeroRegistroPredicate = builder.like(builder.lower(root.get("numeroRegistro")), likePattern);
      Predicate enfermedadesPredicate = builder.like(builder.lower(root.get("enfermedades")), likePattern);

      predicates.add(builder.or(nombrePredicate, chipPredicate, numeroRegistroPredicate, enfermedadesPredicate));
    }
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

    if (nonNull(criteria.getVacunaDesde()) || nonNull(criteria.getVacunaHasta())) {
      Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
      Root<Vacunacion> subRoot = subquery.from(Vacunacion.class);
      subquery.select(builder.greatest(subRoot.<LocalDate>get("fecha")))
          .where(builder.equal(subRoot.get("animal"), root));

      if (nonNull(criteria.getVacunaDesde())) {
        predicates.add(builder.greaterThanOrEqualTo(subquery, criteria.getVacunaDesde()));
      }
      if (nonNull(criteria.getVacunaHasta())) {
        predicates.add(builder.lessThanOrEqualTo(subquery, criteria.getVacunaHasta()));
      }
    }
  }

  private static void addUltimaDesparasitacion(LocalDate ultimaDesparasitacionDesde, LocalDate ultimaDesparasitacionHasta,
      TipoDesparasitacion tipo,
      Root<Animal> root,
      CriteriaQuery<?> query, CriteriaBuilder builder,
      List<Predicate> predicates) {

    if (nonNull(ultimaDesparasitacionDesde) || nonNull(ultimaDesparasitacionHasta)) {
      Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
      Root<Desparasitacion> subRoot = subquery.from(Desparasitacion.class);

      Predicate animalMatch = builder.equal(subRoot.get("animal"), root);
      Predicate tipoInterno = builder.equal(subRoot.get("tipo"), tipo);

      subquery.select(builder.greatest(subRoot.<LocalDate>get("fecha")))
          .where(builder.and(animalMatch, tipoInterno));

      if (nonNull(ultimaDesparasitacionDesde)) {
        predicates.add(builder.greaterThanOrEqualTo(subquery, ultimaDesparasitacionDesde));
      }
      if (nonNull(ultimaDesparasitacionHasta)) {
        predicates.add(builder.lessThanOrEqualTo(subquery, ultimaDesparasitacionHasta));
      }
    }
  }

  private static void addUltimoTest(ListadoAnimalesCriteria criteria, Root<Animal> root,
      CriteriaQuery<?> query, CriteriaBuilder builder,
      List<Predicate> predicates) {

    if (nonNull(criteria.getTestDesde()) || nonNull(criteria.getTestHasta())) {
      Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
      Root<Test> subRoot = subquery.from(Test.class);
      subquery.select(builder.greatest(subRoot.<LocalDate>get("fecha")))
          .where(builder.equal(subRoot.get("animal"), root));

      if (nonNull(criteria.getTestDesde())) {
        predicates.add(builder.greaterThanOrEqualTo(subquery, criteria.getTestDesde()));
      }
      if (nonNull(criteria.getTestHasta())) {
        predicates.add(builder.lessThanOrEqualTo(subquery, criteria.getTestHasta()));
      }
    }
  }

  private static void addSorting(ListadoAnimalesCriteria criteria, CriteriaQuery<?> query, CriteriaBuilder builder, Root<Animal> root) {
    if (nonNull(criteria.getSortBy()) && nonNull(criteria.getSortDirection())) {
      Sort.Direction direction = Sort.Direction.fromString(criteria.getSortDirection());
      if (direction == Sort.Direction.ASC) {
        query.orderBy(builder.asc(root.get(criteria.getSortBy())));
      } else {
        query.orderBy(builder.desc(root.get(criteria.getSortBy())));
      }
    }
  }
}