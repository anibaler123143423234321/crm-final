package com.midas.crm.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codi_usuario")
    private Long id;

    @Column(name = "usuario", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "clave", nullable = false, length = 200)
    private String password;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "dni", nullable = false, length = 8, unique = true)
    private String dni;

    @Column(name = "telefono", nullable = true)
    private String telefono;

    @Column(name = "email", nullable = true, length = 50, unique = true)
    private String email;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    @Column(name = "estado", nullable = false, length = 1)
    private String estado = "A"; // Valor por defecto

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Transient
    private String token;

    @JsonProperty("tokenPassword")
    @Column(name = "tokenPassword", nullable = true)
    private String tokenPassword;

    @Column(name = "deletion_time", nullable = true)
    private LocalDateTime deletionTime;

    @Column(name = "sede", nullable = true, length = 100)
    private String sede; // Nueva columna para la sede a la que pertenece el usuario


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinador_id")
    @JsonBackReference  // Evita la serialización inversa para evitar ciclo
    private User coordinador;

    @OneToMany(mappedBy = "coordinador", fetch = FetchType.LAZY)
    @JsonManagedReference  // Serializa esta colección sin volver a serializar el coordinador
    private List<User> asesores;
}
