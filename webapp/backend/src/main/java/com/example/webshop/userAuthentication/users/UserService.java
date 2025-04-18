package com.example.webshop.userAuthentication.users;

import com.example.webshop.userAuthentication.cookieTable.CookieTable;
import com.example.webshop.userAuthentication.cookieTable.CookieTableService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.webshop.HashingHelper.hashPasswordWithSalt;
import static com.example.webshop.HashingHelper.hashValue;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieTableService cookieTableService;


    @Query("SELECT u FROM User u")
    public List<UserTable> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserTable> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public UserTable getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserTable getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public UserTable saveUser(UserTable user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    public Optional<UserTable> findByUsernamePassword(String username, String password) {
        UserTable user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        String passwordHash = hashPasswordWithSalt(password, user.getSalt());
        List<UserTable> users = userRepository.findByUsernameAndPassword(username, passwordHash);
        return (users != null && !users.isEmpty()) ? Optional.of(users.get(0)) : Optional.empty();
    }

    public String readCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public ResponseEntity<User> authenticate(HttpServletRequest request){
        LocalDateTime currentTime = LocalDateTime.now();
        String cookie = readCookie(request);
        if(cookie == null){
            System.out.println("No Cookie Set");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Optional<CookieTable> optionalCookieTable = cookieTableService.findByCookieData(hashValue(cookie.getBytes()));
        // Did not login
        if(optionalCookieTable.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        CookieTable cookieTable = optionalCookieTable.get();

        if(currentTime.isAfter(cookieTable.getExpiredAt())){
            cookieTableService.deleteById(cookieTable.getCookieId());
            System.out.println("Cookie expired");
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }


        Optional<UserTable> optionalUserTable = getUserById(cookieTable.getUserId());
        if(optionalUserTable.isEmpty()){
            System.out.println("Internal Error: User not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserTable userTable = optionalUserTable.get();
        return new ResponseEntity<>(userTable.getUser(), HttpStatus.OK);
    }

    public User getUser(HttpServletRequest request){
        return authenticate(request).getBody();
    }

}