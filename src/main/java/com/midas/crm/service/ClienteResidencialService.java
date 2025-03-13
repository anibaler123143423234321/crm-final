package com.midas.crm.service;


import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClienteResidencialService {
    List<ClienteResidencial> listarTodos();

    // Cambiar para que retorne una lista en lugar de un Optional
    List<ClienteResidencial> buscarPorMovil(String movil);

    ClienteResidencial obtenerPorId(Long id);


    @Transactional
    ClienteResidencial guardar(ClienteResidencial cliente, Long usuarioId);

    ClienteResidencial actualizar(Long id, ClienteResidencial cliente);

    void eliminar(Long id);

    Page<ClienteConUsuarioDTO> obtenerClientesConUsuario(Pageable pageable);


    // Nuevo método para buscar por móvil
    //Optional<ClienteResidencial> buscarPorMovil(String movilContacto);


    Page<ClienteConUsuarioDTO> obtenerClientesConUsuarioFiltrados(String dniAsesor, String nombreAsesor, String numeroMovil, LocalDate fecha, Pageable pageable);

    Page<ClienteConUsuarioDTO> obtenerClientesConUsuarioFiltradosPorFechaActual(String dniAsesor, String nombreAsesor, String numeroMovil, Pageable pageable);

}