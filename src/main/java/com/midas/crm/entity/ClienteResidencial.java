package com.midas.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente_residencial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResidencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campania", length = 50, nullable = true)
    private String campania;

    @Column(name = "nombres_apellidos", length = 100, nullable = true)
    private String nombresApellidos;

    @Column(name = "nif_nie", length = 20, nullable = true)
    private String nifNie;

    @Column(name = "nacionalidad", length = 30, nullable = true)
    private String nacionalidad;

    @Column(name = "fecha_nacimiento", nullable = true)
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 10, nullable = true)
    private String genero;

    @Column(name = "correo_electronico", length = 100, nullable = true)
    private String correoElectronico;

    @Column(name = "cuenta_bancaria", length = 34, nullable = true)
    private String cuentaBancaria;

    @Column(name = "permanencia", length = 30, nullable = true)
    private String permanencia;

    @Column(name = "direccion", length = 200, nullable = true)
    private String direccion;

    @Column(name = "tipo_fibra", length = 50, nullable = true)
    private String tipoFibra;

    @Column(name = "movil_contacto", length = 20, nullable = false)
    private String movilContacto;

    @Column(name = "fijo_compania", length = 50, nullable = true)
    private String fijoCompania;

    @Column(name = "plan_actual", length = 100, nullable = true)
    private String planActual;

    @Column(name = "codigo_postal", length = 10, nullable = true)
    private String codigoPostal;

    @Column(name = "provincia", length = 50, nullable = true)
    private String provincia;

    @Column(name = "distrito", length = 50, nullable = true)
    private String distrito;

    @Column(name = "ciudad", length = 50, nullable = true)
    private String ciudad;

    @Column(name = "tipo_plan", length = 20, nullable = true)
    private String tipoPlan;

    @Column(name = "icc", length = 19, nullable = true)
    private String icc;

    // Se define la tabla auxiliar para almacenar la colección de móviles a portar
    @ElementCollection
    @CollectionTable(
            name = "cliente_residencial_moviles_aportar",
            joinColumns = @JoinColumn(name = "cliente_residencial_id")
    )
    @Column(name = "movil", length = 255, nullable = true)
    @Builder.Default
    private List<String> movilesAPortar = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "codi_usuario")
    private User usuario;

    @Column(name = "autoriza_seguros", nullable = true)
    @Builder.Default
    private Boolean autorizaSeguros = false;

    @Column(name = "autoriza_energias", nullable = true)
    @Builder.Default
    private Boolean autorizaEnergias = false;

    @Column(name = "venta_realizada", nullable = true)
    @Builder.Default
    private Boolean ventaRealizada = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
