package com.midas.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String refreshToken;
    private Long id;
    private String username;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String role;
    private String sede;
}
