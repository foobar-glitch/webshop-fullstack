package com.example.webshop.userAuthentication.registerTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterTableService {

    @Autowired
    private RegisterTableRepository registerTableRepository;

    public Optional<RegisterTable> findByRegisterToken(String registerToken) {
        return registerTableRepository.findByRegisterToken(registerToken);
    }


}
