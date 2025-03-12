package com.midas.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token único de refresco
    @Column(nullable = false, unique = true)
    private String token;

    // Fecha de expiración del refresh token
    @Column(nullable = false)
    private Instant expiryDate;

    // Relación con el usuario (asumiendo que la columna primaria en User es 'id' o 'codi_usuario')
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "codi_usuario")
    private User user;
}

