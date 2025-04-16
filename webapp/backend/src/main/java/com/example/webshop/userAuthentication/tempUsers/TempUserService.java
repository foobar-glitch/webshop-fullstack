package com.example.webshop.userAuthentication.tempUsers;

import com.example.webshop.userAuthentication.registerTable.RegisterTable;
import com.example.webshop.userAuthentication.registerTable.RegisterTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.webshop.HashingHelper.*;


@Service
public class TempUserService {

    @Autowired
    private TempUserRepository tempUserRepository;

    @Autowired
    private RegisterTableRepository registerTableRepository;


    @Query("SELECT u FROM User u")
    public List<TempUser> getAllUsers() {
        return tempUserRepository.findAll();
    }

    public Optional<TempUser> getUserById(Long userId) {
        return tempUserRepository.findById(userId);
    }

    public Optional<TempUser> getUserByUsername(String username) {
        return tempUserRepository.findByUsername(username);
    }

    public Optional<TempUser> getUserByEmail(String email){
        return tempUserRepository.findByEmail(email);
    }

    public TempUser saveUser(TempUser user) {
        return tempUserRepository.save(user);
    }

    public void deleteUser(Long userId) {
        tempUserRepository.deleteById(userId);
    }


    public void addUserWithPasswordSaltAndRole(String username, String email, String password, String role) {
        // If temp user for username or email already exists delete it
        tempUserRepository.findByUsername(username).ifPresent(tempUser -> tempUserRepository.delete(tempUser));
        tempUserRepository.findByEmail(email).ifPresent(tempUser -> tempUserRepository.delete(tempUser));

        LocalDateTime currentTime = LocalDateTime.now();
        String salt = generateRandomSalt();

        TempUser user = new TempUser();
        user.setUsername(username);
        user.setPasswordHash(hashPasswordWithSalt(password, salt));
        user.setSalt(salt);
        user.setEmail(email);
        user.setRole(role);
        user.setCreatedAt(currentTime);
        user.setUpdatedAt(currentTime);
        tempUserRepository.save(user);

        RegisterTable registerTable = new RegisterTable();
        String randomRegisterToken = UUID.randomUUID().toString();
        System.out.println("Registertoken = " + randomRegisterToken);
        registerTable.setRegisterToken(hashValue(randomRegisterToken.getBytes()));
        registerTable.setCreatedAt(currentTime);
        registerTable.setTempUser(user);
        //Expires in 24 hours
        registerTable.setExpiredAt(currentTime.plusHours(24));
        registerTableRepository.save(registerTable);



    }



}
