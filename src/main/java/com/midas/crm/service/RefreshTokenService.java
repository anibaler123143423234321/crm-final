package com.midas.crm.service;

import com.midas.crm.entity.RefreshToken;
import com.midas.crm.entity.User;
import com.midas.crm.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Duración del refresh token en milisegundos (por ejemplo, 7 días)
    @Value("${app.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        // Verificar si ya existe un refresh token para el usuario
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);
        if (existingTokenOpt.isPresent()) {
            // Actualizamos el token y su expiración
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.setToken(UUID.randomUUID().toString());
            existingToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(existingToken);
        } else {
            // Crear un nuevo refresh token
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(user)
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .token(UUID.randomUUID().toString())
                    .build();
            return refreshTokenRepository.save(refreshToken);
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifica la expiración del token. Si ya expiró, lo elimina y lanza excepción.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Inicia sesión nuevamente");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        User user = new User();
        user.setId(userId);
        return refreshTokenRepository.deleteByUser(user);
    }
}
