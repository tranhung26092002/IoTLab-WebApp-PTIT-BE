package com.ptit.service.domain.repositories;

import com.ptit.service.app.dtos.UserFilter;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.app.responses.user.UserResponse;
import com.ptit.service.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.roleType = 'TEACHER'")
    Page<User> getAllInstructors(Pageable pageable);

    boolean existsByUserName(String userName);

    @Query("SELECT u FROM User u WHERE " +
            "(:#{#filter.id} IS NULL OR u.id = :#{#filter.id}) AND " +
            "(:#{#filter.userName} IS NULL OR u.userName LIKE %:#{#filter.userName}%) AND " +
            "(:#{#filter.fullName} IS NULL OR u.fullName LIKE %:#{#filter.fullName}%) AND " +
            "(:#{#filter.classCode} IS NULL OR u.classCode LIKE %:#{#filter.classCode}%) AND " +
            "(:#{#filter.roleType} IS NULL OR u.roleType = :#{#filter.roleType}) " )
    Page<User> filterUsers(
            @Param("filter") UserFilter filter,
            Pageable pageable);
}
