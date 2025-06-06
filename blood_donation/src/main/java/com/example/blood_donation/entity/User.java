package com.example.blood_donation.entity;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long userID;

    public String username;

//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password invalid!")
    public String password;

    @Email
    public String email;

    @Pattern(regexp = "^(84|0[3|5|7|8|9])[0-9]{8}$", message = "Phone invalid!")
    public String phone;

    public String address;

    @Pattern(regexp = "^\\d{12}$", message = "CCCD invalid!")
    public String cccd;

    @Enumerated(EnumType.STRING)
    public TypeBlood typeBlood;
    @Enumerated(EnumType.STRING)
    public Role role;
    @Enumerated(EnumType.STRING)
    public Gender gender;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

}
