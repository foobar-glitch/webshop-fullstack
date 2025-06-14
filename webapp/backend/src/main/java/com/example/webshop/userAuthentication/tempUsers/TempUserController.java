package com.example.webshop.userAuthentication.tempUsers;

import com.example.webshop.userAuthentication.registerTable.RegisterTable;
import com.example.webshop.userAuthentication.registerTable.RegisterTableRepository;
import com.example.webshop.userAuthentication.users.UserService;
import com.example.webshop.userAuthentication.users.UserTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.webshop.HashingHelper.hashValue;

@RestController
public class TempUserController {

    @Autowired
    private TempUserService tempUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private RegisterTableRepository registerTableService;


    /**
     * Registers User to the temporary database
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUser registerUser){
        String username = registerUser.getUsername();
        String email = registerUser.getEmail();
        String password = registerUser.getPassword();
        String confirmPassword = registerUser.getConfirmPassword();

        if(!password.equals(confirmPassword)){
            System.out.println("Passwords do not match");
            return new ResponseEntity<>("Passwords do not match",HttpStatus.FORBIDDEN);
        }
        if(userService.getUserByUsername(username) != null){
            System.out.println("User already exists");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if(userService.getUserByEmail(email) != null){
            System.out.println("E-Mail already exists");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Optional<TempUser> tempUser = tempUserService.getUserByUsername(username);
        if(tempUser.isPresent()){
            System.out.println("Temp user already registered delete user");
            tempUserService.deleteUser(tempUser.get().getTempUserId());
        }

        tempUserService.addUserWithPasswordSaltAndRole(username, email, password, "user");
        return new ResponseEntity<>("Check Register Token on /register/validate?token=",HttpStatus.OK);
    }


    @PostMapping("/register/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token){
        String hashedToken = hashValue(token.getBytes());
        Optional<RegisterTable> optionalRegisterTable = registerTableService.findByRegisterToken(hashedToken);
        if(optionalRegisterTable.isEmpty()){
            System.out.println("This entry does not exist");
            return new ResponseEntity<>("This entry does not exist",HttpStatus.FORBIDDEN);
        }
        RegisterTable registerTable = optionalRegisterTable.get();

        //check table time...
        LocalDateTime currentTime = LocalDateTime.now();
        if(currentTime.isAfter(registerTable.getExpiredAt())){
            System.out.println("Register Entry expired");
            registerTableService.delete(registerTable);
            return new ResponseEntity<>("Register Entry expired",HttpStatus.FORBIDDEN);
        }

        // Inserting temp user to user
        TempUser tempUser = registerTable.getTempUser();
        UserTable user = new UserTable();
        user.setEmail(tempUser.getEmail());
        user.setRole(tempUser.getRole());
        user.setSalt(tempUser.getSalt());
        user.setUsername(tempUser.getUsername());
        user.setPasswordHash(tempUser.getPasswordHash());
        user.setCreatedAt(tempUser.getCreatedAt());
        user.setUpdatedAt(tempUser.getUpdatedAt());

        userService.saveUser(user);
        registerTableService.delete(registerTable);
        tempUserService.deleteUser(tempUser.getTempUserId());
        return new ResponseEntity<>("User Created successfully",HttpStatus.OK);
    }
}
