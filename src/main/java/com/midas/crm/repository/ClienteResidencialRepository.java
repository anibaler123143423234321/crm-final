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

}
