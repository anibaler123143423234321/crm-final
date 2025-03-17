package com.midas.crm.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResidencialDTO {
    private Long id;
    private String campania;
    private String nombresApellidos;
    private String nifNie;
    private String nacionalidad;
    private LocalDate fechaNacimiento;
    private String genero;
    private String correoElectronico;
    private String movilContacto;
    private String direccion;
    private String codigoPostal;
    private String provincia;
    private String ciudad;
    private String tipoPlan;
    private Boolean ventaRealizada;
    private LocalDateTime fechaCreacion;
    private List<String> movilesAPortar;
}