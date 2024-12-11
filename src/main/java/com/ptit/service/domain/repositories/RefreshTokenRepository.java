package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String jwtRefreshToken);
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id = :id")
    List<RefreshToken> findAllValidTokenByUserId(Long id);
}
