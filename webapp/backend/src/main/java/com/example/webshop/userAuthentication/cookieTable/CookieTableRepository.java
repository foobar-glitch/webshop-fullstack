package com.example.webshop.userAuthentication.cookieTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CookieTableRepository extends JpaRepository<CookieTable, Long> {
    Optional<CookieTable> findByCookieData(String cookieData);
    List<CookieTable> findByUserId(Long userId);
}
