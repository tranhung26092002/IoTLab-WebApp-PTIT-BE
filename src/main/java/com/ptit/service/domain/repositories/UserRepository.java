package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u " +
            "JOIN PasswordResetToken prt ON u.id = prt.user.id " +
            "WHERE prt.token = :passwordToken " +
            "AND prt.expiryDate > CURRENT_TIMESTAMP")
    Optional<User> findByPasswordToken(@Param("passwordToken") String passwordToken);

    User findUserByUserName(String userName);

    User findUserByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
