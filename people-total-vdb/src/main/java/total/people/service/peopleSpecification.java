package total.people.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import total.people.entity.People;

public class peopleSpecification {

    public static Specification<People> getSpecifications(Map<String, String> allParams) {
        return new Specification<People>() {
            @Override
            public Predicate toPredicate(Root<People> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                allParams.forEach((key, value) -> {
                    if (value != null && !value.trim().isEmpty()) {
                        if (key.startsWith("include_")) {
                            String actualKey = key.substring(8);
                            if (actualKey.equals("employee_size")) {
                                String[] sizeValues = value.split(",");
                                List<Predicate> sizePredicates = new ArrayList<>();
                                for (String size : sizeValues) {
                                    sizePredicates.add(criteriaBuilder.like(root.get(actualKey), "%" + size.trim() + "%"));
                                }
                                predicates.add(criteriaBuilder.or(sizePredicates.toArray(new Predicate[0])));
                            } else {
                                String[] values = value.split(",");
                                List<Predicate> valuePredicates = new ArrayList<>();
                                for (String v : values) {
                                    valuePredicates.add(criteriaBuilder.equal(root.get(actualKey), v.trim()));
                                }
                                predicates.add(criteriaBuilder.or(valuePredicates.toArray(new Predicate[0])));
                            }
                        } else if (key.startsWith("exclude_")) {
                            String actualKey = key.substring(8);
                            String[] values = value.split(",");
                            List<Predicate> valuePredicates = new ArrayList<>();
                            for (String v : values) {
                                valuePredicates.add(criteriaBuilder.notEqual(root.get(actualKey), v.trim()));
                            }
                            predicates.add(criteriaBuilder.and(valuePredicates.toArray(new Predicate[0])));
                        } else {
                            predicates.add(criteriaBuilder.equal(root.get(key), value.trim()));
                        }
                    }
                });

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
