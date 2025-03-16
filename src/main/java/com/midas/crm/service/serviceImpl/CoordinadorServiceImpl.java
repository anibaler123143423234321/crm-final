package com.midas.crm.service.serviceImpl;


import com.midas.crm.entity.DTO.AsesorDTO;
import com.midas.crm.entity.DTO.AsignacionAsesorDTO;
import com.midas.crm.entity.DTO.CoordinadorDTO;
import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.service.CoordinadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoordinadorServiceImpl implements CoordinadorService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public CoordinadorDTO asignarAsesoresACoordinador(AsignacionAsesorDTO asignacionDTO) {
        // Verificar que el coordinador existe
        User coordinador = userRepository.findById(asignacionDTO.getCoordinadorId())
                .orElseThrow(() -> new RuntimeException("Coordinador no encontrado"));

        // Verificar que el usuario es un coordinador
        if (coordinador.getRole() != Role.COORDINADOR) {
            throw new RuntimeException("El usuario no es un coordinador");
        }

        // Obtener los asesores a asignar
        List<User> asesores = userRepository.findAllById(asignacionDTO.getAsesorIds());

        // Verificar que todos los asesores existen
        if (asesores.size() != asignacionDTO.getAsesorIds().size()) {
            throw new RuntimeException("Uno o más asesores no fueron encontrados");
        }

        // Verificar que todos son asesores y asignarles el coordinador
        for (User asesor : asesores) {
            if (asesor.getRole() != Role.ASESOR) {
                throw new RuntimeException("El usuario " + asesor.getUsername() + " no es un asesor");
            }
            asesor.setCoordinador(coordinador);
            userRepository.save(asesor);
        }

        // Recargar el coordinador para obtener la lista actualizada de asesores
        coordinador = userRepository.findById(coordinador.getId()).orElseThrow();

        // Convertir a DTO y retornar
        return convertirACoordinadorDTO(coordinador);
    }

    @Override
    public List<CoordinadorDTO> obtenerTodosLosCoordinadores() {
        List<User> coordinadores = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.COORDINADOR)
                .collect(Collectors.toList());

        return coordinadores.stream()
                .map(this::convertirACoordinadorDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CoordinadorDTO obtenerCoordinadorPorId(Long coordinadorId) {
        User coordinador = userRepository.findById(coordinadorId)
                .orElseThrow(() -> new RuntimeException("Coordinador no encontrado"));

        if (coordinador.getRole() != Role.COORDINADOR) {
            throw new RuntimeException("El usuario no es un coordinador");
        }

        return convertirACoordinadorDTO(coordinador);
    }

    @Override
    public List<AsesorDTO> obtenerAsesoresSinCoordinador() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ASESOR && user.getCoordinador() == null)
                .map(this::convertirAAsesorDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean eliminarAsesorDeCoordinador(Long coordinadorId, Long asesorId) {
        User coordinador = userRepository.findById(coordinadorId)
                .orElseThrow(() -> new RuntimeException("Coordinador no encontrado"));

        User asesor = userRepository.findById(asesorId)
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));

        if (asesor.getCoordinador() != null && asesor.getCoordinador().getId().equals(coordinadorId)) {
            asesor.setCoordinador(null);
            userRepository.save(asesor);
            return true;
        }

        return false;
    }

    // Métodos auxiliares para convertir entidades a DTOs

    private CoordinadorDTO convertirACoordinadorDTO(User coordinador) {
        CoordinadorDTO dto = new CoordinadorDTO();
        dto.setId(coordinador.getId());
        dto.setNombre(coordinador.getNombre());
        dto.setApellido(coordinador.getApellido());
        dto.setUsername(coordinador.getUsername());
        dto.setDni(coordinador.getDni());
        dto.setEmail(coordinador.getEmail());
        dto.setTelefono(coordinador.getTelefono());
        dto.setSede(coordinador.getSede());

        if (coordinador.getAsesores() != null) {
            dto.setAsesores(coordinador.getAsesores().stream()
                    .map(this::convertirAAsesorDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setAsesores(new ArrayList<>());
        }

        return dto;
    }

    private AsesorDTO convertirAAsesorDTO(User asesor) {
        AsesorDTO dto = new AsesorDTO();
        dto.setId(asesor.getId());
        dto.setNombre(asesor.getNombre());
        dto.setApellido(asesor.getApellido());
        dto.setUsername(asesor.getUsername());
        dto.setDni(asesor.getDni());
        dto.setEmail(asesor.getEmail());
        dto.setTelefono(asesor.getTelefono());
        dto.setSede(asesor.getSede());
        return dto;
    }
}