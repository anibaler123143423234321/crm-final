package com.midas.crm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClienteConUsuarioDTO {
    private String dni;
    private String nombres;
    private LocalDateTime fechaIngresado;  // Changed to LocalDateTime
    private String numeroMovil;

    public ClienteConUsuarioDTO(String dni, String nombres, LocalDateTime fechaIngresado, String numeroMovil) {
        this.dni = dni;
        this.nombres = nombres;
        this.fechaIngresado = fechaIngresado;
        this.numeroMovil = numeroMovil;
    }
}
