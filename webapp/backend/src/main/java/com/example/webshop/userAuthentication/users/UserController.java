package com.example.webshop.userAuthentication.users;

import com.example.webshop.userAuthentication.cookieTable.CookieTable;
import com.example.webshop.userAuthentication.cookieTable.CookieTableService;
import com.example.webshop.userAuthentication.resetTable.ResetTable;
import com.example.webshop.userAuthentication.resetTable.ResetTableService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.example.webshop.HashingHelper.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CookieTableService cookieTableService;

    @Autowired
    private ResetTableService resetTableService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password,
                                        HttpServletResponse response){
        String cookieId = setTokenCookie(response).getValue();
        if(cookieId == null){
            System.out.println("No Cookie set");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<UserTable> optionalUserTable= userService.findByUsernamePassword(username, password);
        if(optionalUserTable.isEmpty()){
            return new ResponseEntity<>("User and password dont match",HttpStatus.FORBIDDEN);
        }
        UserTable userTable = optionalUserTable.get();
        LocalDateTime currentTime = LocalDateTime.now();

        CookieTable cookieTable = new CookieTable();

        /// If the entry already exists delete it
        Optional<CookieTable> optionalCookieTable = cookieTableService.findByCookieData(hashValue(cookieId.getBytes()));
        optionalCookieTable.ifPresent(table -> cookieTableService.deleteById(table.getCookieId()));
        /// If user has cookie entry delete cookie entry before creating new one
        for(CookieTable c: cookieTableService.findByUserId(userTable.getUserId())){
            cookieTableService.deleteById(c.getCookieId());
        }

        cookieTable.setUserId(userTable.getUserId());
        cookieTable.setCookieData(hashValue(cookieId.getBytes()));
        cookieTable.setCreatedAt(currentTime);
        cookieTable.setExpiredAt(currentTime.plusMinutes(10));

        cookieTableService.save(cookieTable);
        return new ResponseEntity<>("Login successful", HttpStatus.ACCEPTED);
    }


    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        User user = userService.authenticate(request).getBody();
        if(user == null){
            return ResponseEntity.ok("Was not logged in before");
        }
        for(CookieTable c: cookieTableService.findByUserId(user.getUserId())){
            cookieTableService.deleteById(c.getCookieId());
        }
        return ResponseEntity.ok("Deleted cookie from table");
    }

    private Cookie setTokenCookie(HttpServletResponse response) {
        // Generate a random token using UUID
        String token = UUID.randomUUID().toString();

        // Create a cookie to store the token
        Cookie cookie = new Cookie("token", token);

        // Set the cookie's max age (in seconds)
        cookie.setMaxAge(60 * 60); // 1 hour

        // Optionally set the cookie's path
        cookie.setPath("/");

        // Set the cookie to be accessible over HTTP only (to prevent JavaScript access)
        cookie.setHttpOnly(true);

        // Add the cookie to the response
        response.addCookie(cookie);

        return cookie;
    }

    @GetMapping("/read-cookie")
    public String readCookie(HttpServletRequest request) {
        return userService.readCookie(request);
    }


    //Authenticate using cookie
    @GetMapping("/authenticate")
    public ResponseEntity<User> authenticate(HttpServletRequest request){
        return userService.authenticate(request);
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotUser(@RequestParam String email){
        LocalDateTime currentTime = LocalDateTime.now();
        UserTable userTable = userService.getUserByEmail(email);
        if(userTable == null){
            return new ResponseEntity<>("E-Mail not found", HttpStatus.OK);
        }

        String resetToken = UUID.randomUUID().toString();
        System.out.println("Forgot Password Token: " + resetToken);

        ResetTable resetTable = new ResetTable();
        resetTable.setUserId(userTable.getUserId());
        resetTable.setResetToken(hashValue(resetToken.getBytes()));
        resetTable.setCreatedAt(currentTime);
        resetTable.setExpiredAt(currentTime.plusHours(24));
        resetTableService.save(resetTable);

        return new ResponseEntity<>("Go to reset-password.html and enter the Reset Token", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetUser(@RequestParam String token, String password, String confirmPassword){
        if(!Objects.equals(password, confirmPassword)){
            return new ResponseEntity<>("Passwords dont match", HttpStatus.BAD_REQUEST);
        }
        Optional<ResetTable> optionalResetTable = resetTableService.findByResetToken(hashValue(token.getBytes()));
        if(optionalResetTable.isEmpty()){
            return new ResponseEntity<>("Token not Found", HttpStatus.FORBIDDEN);
        }
        ResetTable resetTable =optionalResetTable.get();
        Optional<UserTable> optionalUserTable = userService.getUserById(resetTable.getUserId());
        if(optionalUserTable.isEmpty()){
            return new ResponseEntity<>("UserID not found Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        UserTable userTable = optionalUserTable.get();
        String newSalt = generateRandomSalt();
        userTable.setSalt(newSalt);
        userTable.setPasswordHash(hashPasswordWithSalt(password, newSalt));
        userTable.setUpdatedAt(LocalDateTime.now());

        // overwrite user
        userService.saveUser(userTable);
        resetTableService.deleteById(resetTable.getResetId());
        return new ResponseEntity<>("Password Reset", HttpStatus.OK);
    }


    @GetMapping("/username/{userId}")
    public ResponseEntity<String> getUsername(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(user.getUsername()))
                .orElse(ResponseEntity.notFound().build());
    }

}