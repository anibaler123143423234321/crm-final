package com.midas.crm.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinadorDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String username;
    private String dni;
    private String email;
    private String telefono;
    private String sede;
    private List<AsesorDTO> asesores;
}