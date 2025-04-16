package com.example.webshop.userAuthentication.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserTable, Long> {
    // You can add custom queries here if necessary
    UserTable findByUsername(String username);

    UserTable findByEmail(String email);
    //password hash (using salt)
    List<UserTable> findByUsernameAndPassword(String username, String password);

}
