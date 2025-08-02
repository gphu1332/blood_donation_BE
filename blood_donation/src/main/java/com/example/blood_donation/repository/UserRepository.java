package com.example.blood_donation.repository;

import com.example.blood_donation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // ✅ Đã sửa từ findByID → findById (phù hợp với field "id" trong entity User)
    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // ✅ Sửa lại field thành "id" thay vì "userID"
    boolean existsByUsernameAndIdNotAndIsDeletedFalse(String username, Long id);

    boolean existsByEmailAndIdNotAndIsDeletedFalse(String email, Long id);

    boolean existsByCccdAndIdNotAndIsDeletedFalse(String cccd, Long id);

    // ✅ Lấy danh sách user chưa bị xóa
    List<User> findAllByIsDeletedFalse();

    // ✅ Tìm user chưa bị xóa theo ID
    Optional<User> findByIdAndIsDeletedFalse(Long id);
}
