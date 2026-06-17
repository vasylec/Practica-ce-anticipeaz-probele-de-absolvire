package com.example.data.repository;

import com.example.data.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("""
            SELECT c FROM Customer c
            LEFT JOIN AppUser u ON u.customer = c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(c.secondName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(u.username) LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    List<Customer> searchCustomers(@Param("filter") String filter);

    @Query("""
            SELECT c FROM Customer c
            LEFT JOIN AppUser u ON u.customer = c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(c.secondName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(u.username) LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    Page<Customer> searchCustomers(@Param("filter") String filter, Pageable pageable);

    @Query("""
            SELECT COUNT(c) FROM Customer c
            LEFT JOIN AppUser u ON u.customer = c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(c.secondName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(u.username) LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    Long count(@Param("filter") String filter);
}
