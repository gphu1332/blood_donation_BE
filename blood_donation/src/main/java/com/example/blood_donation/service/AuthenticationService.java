package com.example.blood_donation.service;

import com.example.blood_donation.dto.LoginRequest;
import com.example.blood_donation.dto.RegisterRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.AuthenticationException;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.MEMBER);
        user.setDeleted(false); // đảm bảo default là false khi đăng ký

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
        } catch (Exception e) {
            throw new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        User user = authenticationRepository.findByUsername(loginRequest.getUsername());

        // ✅ Kiểm tra tài khoản đã bị xoá
        if (user.isDeleted()) {
            throw new AuthenticationException("Tài khoản này đã bị vô hiệu hoá!");
        }

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        // Generate token dựa trên tùy chọn rememberMe
        String token = tokenService.generateToken(user, loginRequest.isRememberMe());
        userDTO.setToken(token);
        return userDTO;
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authenticationRepository.findByUsername(username);
        if (user.isDeleted()) {
            throw new AuthenticationException("Tài khoản hiện tại đã bị vô hiệu hoá!");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = authenticationRepository.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại hoặc đã bị xoá!");
        }
        return user;
    }
}
