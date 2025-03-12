package com.midas.crm.repository;

import com.midas.crm.entity.RefreshToken;
import com.midas.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    int deleteByUser(User user);
}
