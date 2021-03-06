package br.com.qisi.skeleton.utils.base.utils.filter;

import br.com.qisi.skeleton.utils.base.repository.QueryParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class GreaterThanFilter<T> implements BaseQueryFilter {

  private static final String GREATER_THAN_OPERATOR = ">";
  private Predicate predicate;
  private final CriteriaBuilder criteriaBuilder;
  private final Root<T> root;
  private final List<QueryParam> queryParams;
  private Class<T> clazz;
  private final BaseQueryFilter nextFilter;

  public GreaterThanFilter(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root, List<QueryParam> queryParams, Class<T> clazz, BaseQueryFilter nextFilter) {
    this.predicate = predicate;
    this.criteriaBuilder = criteriaBuilder;
    this.root = root;
    this.queryParams = queryParams;
    this.clazz = clazz;
    this.nextFilter = nextFilter;
  }

  @Override
  public Predicate agregatePredicate() {

    for (QueryParam queryParam : queryParams) {

      if (GREATER_THAN_OPERATOR.equalsIgnoreCase(queryParam.getOperation())) {

        String[] splitedFieldName = queryParam.getFieldName().split("[.]");

        if(this.needJoin(splitedFieldName)){
          Join<T, ?> join = doJoin(splitedFieldName, root);
          queryParam.setFieldName(splitedFieldName[1]);

          Class fieldClass = this.getFieldClass(queryParam.getFieldName(), clazz);

          if(LocalDateTime.class == fieldClass) {
            add(predicate, criteriaBuilder.greaterThan(join.get(queryParam.getFieldName()), LocalDateTime.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE_TIME)));
          } else if (LocalDate.class == fieldClass){
            add(predicate, criteriaBuilder.greaterThan(join.get(queryParam.getFieldName()), LocalDate.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE)));
          } else {
            add(predicate, criteriaBuilder.greaterThan(join.get(queryParam.getFieldName()), queryParam.getFieldValue()));
          }
        } else {

          Class fieldClass = this.getFieldClass(queryParam.getFieldName(), clazz);

          if(LocalDateTime.class == fieldClass) {
            add(predicate, criteriaBuilder.greaterThan(root.get(queryParam.getFieldName()), LocalDateTime.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE_TIME)));
          } else if (LocalDate.class == fieldClass){
            add(predicate, criteriaBuilder.greaterThan(root.get(queryParam.getFieldName()), LocalDate.parse(queryParam.getFieldValue(), DateTimeFormatter.ISO_DATE)));
          } else {
            add(predicate, criteriaBuilder.greaterThan(root.get(queryParam.getFieldName()), queryParam.getFieldValue()));
          }
        }
      }

    }

    if (!Objects.isNull(this.nextFilter)) {
      return this.nextFilter.agregatePredicate();
    } else {
      return predicate;
    }
  }
}
