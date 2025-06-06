package com.example.blood_donation.service;

import com.example.blood_donation.dto.LoginRequest;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.repositoty.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = authenticationRepository.save(user);
        return newUser;
    }

    public User login(LoginRequest loginRequest) {
       try {
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                   loginRequest.getUsername(),
                   loginRequest.getPassword()
           ));
       }catch (Exception e){
           System.out.println("Sai thông tin đăng nhập!!!");
       }
       return authenticationRepository.findByUsername(loginRequest.getUsername());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationRepository.findByUsername(username);
    }
}
