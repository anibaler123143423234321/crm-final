package com.midas.crm.service;


import com.midas.crm.entity.DTO.AsesorDTO;
import com.midas.crm.entity.DTO.AsignacionAsesorDTO;
import com.midas.crm.entity.DTO.CoordinadorDTO;
import com.midas.crm.entity.User;

import java.util.List;

public interface CoordinadorService {
    CoordinadorDTO asignarAsesoresACoordinador(AsignacionAsesorDTO asignacionDTO);
    List<CoordinadorDTO> obtenerTodosLosCoordinadores();
    CoordinadorDTO obtenerCoordinadorPorId(Long coordinadorId);
    List<AsesorDTO> obtenerAsesoresSinCoordinador();
    boolean eliminarAsesorDeCoordinador(Long coordinadorId, Long asesorId);
}