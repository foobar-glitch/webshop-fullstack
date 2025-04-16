package com.example.webshop.userAuthentication.resetTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTableRepository extends JpaRepository<ResetTable, Long> {
    Optional<ResetTable> findByResetToken(String resetToken);
}
