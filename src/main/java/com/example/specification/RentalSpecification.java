package com.example.specification;

import com.example.data.entity.Customer;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RentalSpecification {
    public static Specification<Rental> search(String filter) {
        return (root, query, cb) -> {
            if (filter == null || filter.isBlank()) {
                return cb.conjunction();
            }
            Join<Rental, Vehicle> v = root.join("vehicle", JoinType.LEFT);
            Join<Rental, Customer> c = root.join("customer", JoinType.LEFT);

            String[] words = filter.trim().split("\\s+");

            List<Predicate> andPredicates = new ArrayList<>();

            for (String word : words) {
                String pattern = word + "%";

                Predicate orPredicate = cb.or(
                        cb.like(cb.lower(v.get("manufacturer")), pattern.toLowerCase()),
                        cb.like(cb.lower(v.get("model")), pattern.toLowerCase()),
                        cb.like(cb.lower(v.get("licensePlate")), pattern.toLowerCase()),
                        cb.like(cb.lower(c.get("firstName")), pattern.toLowerCase()),
                        cb.like(cb.lower(c.get("secondName")), pattern.toLowerCase()),
                        cb.like(cb.lower(c.get("phone")), pattern.toLowerCase()));

                andPredicates.add(orPredicate);
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }
}
