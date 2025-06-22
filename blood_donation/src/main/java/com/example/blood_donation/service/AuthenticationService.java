package com.example.blood_donation.service;

import com.example.blood_donation.dto.LoginRequest;
import com.example.blood_donation.dto.RegisterRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.AuthenticationException;
import com.example.blood_donation.repositoty.AuthenticationRepository;
import org.modelmapper.ModelMapper;
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

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private TokenService tokenService;

    public UserDTO register(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.MEMBER);

        User savedUser = authenticationRepository.save(user);

        String token = tokenService.generateToken(savedUser);

        UserDTO dto = modelMapper.map(savedUser, UserDTO.class);
        dto.setToken(token);

        return dto;
    }

    public UserDTO login(LoginRequest loginRequest) {
       try {
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                   loginRequest.getUsername(),
                   loginRequest.getPassword()
           ));
       }catch (Exception e){
           throw new AuthenticationException("Invalid username or password!!");
       }
       User user = authenticationRepository.findByUsername(loginRequest.getUsername());
       UserDTO userDTO = modelMapper.map(user, UserDTO.class);
       String token = tokenService.generateToken(user);
       userDTO.setToken(token);
       return userDTO;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationRepository.findByUsername(username);
    }
}
