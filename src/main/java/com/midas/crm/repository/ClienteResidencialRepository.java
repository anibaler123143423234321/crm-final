package com.midas.crm.repository;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteResidencialRepository extends JpaRepository<ClienteResidencial, Long> {

    @Query("SELECT new com.midas.crm.entity.ClienteConUsuarioDTO(" +
            "u.dni, CONCAT(u.nombre, ' ', u.apellido), cr.fechaCreacion, cr.movilContacto) " +
            "FROM ClienteResidencial cr JOIN cr.usuario u")
    Page<ClienteConUsuarioDTO> obtenerClientesConUsuario(Pageable pageable);


    // Cambia el método para permitir duplicados
    List<ClienteResidencial> findByMovilContacto(String movilContacto);

    @Query("SELECT new com.midas.crm.entity.ClienteConUsuarioDTO(" +
            "u.dni, CONCAT(u.nombre, ' ', u.apellido), cr.fechaCreacion, cr.movilContacto) " +
            "FROM ClienteResidencial cr " +
            "JOIN cr.usuario u " +
            "WHERE (:dniAsesor IS NULL OR :dniAsesor = '' OR u.dni = :dniAsesor) " +
            "AND (COALESCE(:nombreAsesor, '') = '' OR CONCAT(u.nombre, ' ', u.apellido) LIKE CONCAT('%', :nombreAsesor, '%')) " +
            "AND (:numeroMovil IS NULL OR :numeroMovil = '' OR cr.movilContacto = :numeroMovil) " +
            "AND (:fecha IS NULL OR DATE(cr.fechaCreacion) = :fecha)")
    Page<ClienteConUsuarioDTO> obtenerClientesConUsuarioFiltrados(
            @Param("dniAsesor") String dniAsesor,
            @Param("nombreAsesor") String nombreAsesor,
            @Param("numeroMovil") String numeroMovil,
            @Param("fecha") LocalDate fecha,
            Pageable pageable);


    @Query("SELECT new com.midas.crm.entity.ClienteConUsuarioDTO(" +
            "u.dni, CONCAT(u.nombre, ' ', u.apellido), cr.fechaCreacion, cr.movilContacto) " +
            "FROM ClienteResidencial cr " +
            "JOIN cr.usuario u " +
            "WHERE (:dniAsesor IS NULL OR :dniAsesor = '' OR u.dni = :dniAsesor) " +
            "AND (COALESCE(:nombreAsesor, '') = '' OR CONCAT(u.nombre, ' ', u.apellido) LIKE CONCAT('%', :nombreAsesor, '%')) " +
            "AND (:numeroMovil IS NULL OR :numeroMovil = '' OR cr.movilContacto = :numeroMovil) " +
            "AND DATE(cr.fechaCreacion) = CURRENT_DATE")
    Page<ClienteConUsuarioDTO> obtenerClientesConUsuarioFiltradosPorFechaActual(
            @Param("dniAsesor") String dniAsesor,
            @Param("nombreAsesor") String nombreAsesor,
            @Param("numeroMovil") String numeroMovil,
            Pageable pageable);

    List<ClienteResidencial> findByFechaCreacionBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Encuentra todos los clientes residenciales asociados a un usuario (asesor) específico
     * @param usuarioId ID del usuario (asesor)
     * @return Lista de clientes residenciales
     */
    List<ClienteResidencial> findByUsuarioId(Long usuarioId);

    /**
     * Cuenta el número de clientes residenciales asociados a un usuario (asesor) específico
     * @param usuarioId ID del usuario (asesor)
     * @return Número de clientes
     */
    @Query("SELECT COUNT(c) FROM ClienteResidencial c WHERE c.usuario.id = :usuarioId")
    Long countClientesByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Encuentra todos los clientes residenciales con venta realizada asociados a un usuario (asesor)
     * @param usuarioId ID del usuario (asesor)
     * @return Lista de clientes con venta realizada
     */
    @Query("SELECT c FROM ClienteResidencial c WHERE c.usuario.id = :usuarioId AND c.ventaRealizada = true")
    List<ClienteResidencial> findVentasRealizadasByUsuarioId(@Param("usuarioId") Long usuarioId);

}