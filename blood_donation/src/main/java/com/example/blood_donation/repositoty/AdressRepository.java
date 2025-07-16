package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Adress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdressRepository extends JpaRepository<Adress, Long> {
    Optional<Adress> findByLatitudeAndLongitude(Double latitude, Double longitude);

    Adress findByLatitudeAndLongitudeAndName(Double latitude, Double longitude, String name);
}
