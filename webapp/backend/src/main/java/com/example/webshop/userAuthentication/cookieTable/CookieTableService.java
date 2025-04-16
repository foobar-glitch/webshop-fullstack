package com.example.webshop.userAuthentication.cookieTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CookieTableService {
    @Autowired
    private CookieTableRepository cookieTableRepository;

    public Optional<CookieTable> findByCookieData(String cookieData){
        return cookieTableRepository.findByCookieData(cookieData);
    }

    public List<CookieTable> findByUserId(Long userId){
        return cookieTableRepository.findByUserId(userId);
    }

    public CookieTable save(CookieTable cookieTable) {
        return cookieTableRepository.save(cookieTable);
    }

    public Optional<CookieTable> findById(Long cookieId) {
        return cookieTableRepository.findById(cookieId);
    }

    public void deleteById(Long cookieId) {
        cookieTableRepository.deleteById(cookieId);
    }

}
