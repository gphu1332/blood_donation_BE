package com.example.blood_donation.service;

import com.example.blood_donation.entity.User;
import com.example.blood_donation.repositoty.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //danh dau day la lop service
public class AuthenticationService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    public User register(User user) {
        User newUser = authenticationRepository.save(user);
        return newUser;
    }
}
