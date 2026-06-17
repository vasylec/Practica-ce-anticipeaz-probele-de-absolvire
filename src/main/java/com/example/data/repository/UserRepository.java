package com.example.data.repository;

import com.example.data.entity.AppUser;
import com.example.data.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);

    AppUser findByEmail(String email);

    AppUser findByCustomer(Customer customer);

    AppUser findAppUserByUsernameOrEmail(String username, String email);
}
