package com.midas.crm.service;

import com.midas.crm.entity.DTO.AsesorDTO;
import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;

import java.util.List;
import java.util.Optional;

public interface AsesorService {

    /**
     * Obtiene todos los usuarios con rol de ASESOR
     * @return Lista de DTOs de asesores
     */
    List<AsesorDTO> getAllAsesores();

    /**
     * Obtiene un asesor por su ID
     * @param id ID del asesor
     * @return DTO del asesor si existe
     */
    Optional<AsesorDTO> getAsesorById(Long id);

    /**
     * Obtiene todos los asesores asignados a un coordinador
     * @param coordinadorId ID del coordinador
     * @return Lista de DTOs de asesores
     */
    List<AsesorDTO> getAsesoresByCoordinadorId(Long coordinadorId);

    /**
     * Convierte una entidad User a un DTO de Asesor
     * @param user Entidad User
     * @return DTO de Asesor
     */
    AsesorDTO convertToDTO(User user);
}