package com.example.webshop.userAuthentication.tempUsers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TempUserRepository extends JpaRepository<TempUser, Long> {
    Optional<TempUser> findByUsername(String username);
    Optional<TempUser> findByEmail(String email);
}
