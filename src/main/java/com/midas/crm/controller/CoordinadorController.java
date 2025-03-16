package com.midas.crm.controller;


import com.midas.crm.entity.DTO.AsesorDTO;
import com.midas.crm.entity.DTO.AsignacionAsesorDTO;
import com.midas.crm.entity.DTO.AsignacionMasivaDTO;
import com.midas.crm.entity.DTO.CoordinadorDTO;
import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.service.CoordinadorService;
import com.midas.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/coordinadores")
public class CoordinadorController {

    private final CoordinadorService coordinadorService;

    private final UserService userService;

    @PostMapping("/asignar-asesores")
    public ResponseEntity<?> asignarAsesoresACoordinador(@RequestBody AsignacionAsesorDTO asignacionDTO) {
        try {
            CoordinadorDTO coordinador = coordinadorService.asignarAsesoresACoordinador(asignacionDTO);
            return ResponseEntity.ok(coordinador);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al asignar asesores: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CoordinadorDTO>> obtenerTodosLosCoordinadores() {
        List<CoordinadorDTO> coordinadores = coordinadorService.obtenerTodosLosCoordinadores();
        return ResponseEntity.ok(coordinadores);
    }

    @GetMapping("/{coordinadorId}")
    public ResponseEntity<?> obtenerCoordinadorPorId(@PathVariable Long coordinadorId) {
        try {
            CoordinadorDTO coordinador = coordinadorService.obtenerCoordinadorPorId(coordinadorId);
            return ResponseEntity.ok(coordinador);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error al obtener coordinador: " + e.getMessage());
        }
    }

    @GetMapping("/asesores-disponibles")
    public ResponseEntity<List<AsesorDTO>> obtenerAsesoresSinCoordinador() {
        List<AsesorDTO> asesores = coordinadorService.obtenerAsesoresSinCoordinador();
        return ResponseEntity.ok(asesores);
    }

    @DeleteMapping("/{coordinadorId}/asesores/{asesorId}")
    public ResponseEntity<?> eliminarAsesorDeCoordinador(
            @PathVariable Long coordinadorId,
            @PathVariable Long asesorId) {
        try {
            boolean eliminado = coordinadorService.eliminarAsesorDeCoordinador(coordinadorId, asesorId);
            if (eliminado) {
                return ResponseEntity.ok("Asesor removido del coordinador exitosamente");
            } else {
                return ResponseEntity.badRequest().body("El asesor no pertenece a este coordinador");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar asesor: " + e.getMessage());
        }
    }

    @PostMapping("/asignar-masivo")
    public ResponseEntity<?> asignarAsesoresMasivo(@RequestBody AsignacionMasivaDTO asignacionDTO) {
        try {
            User coordinador = userService.findUserById(asignacionDTO.getCoordinadorId());

            if (coordinador == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coordinador no encontrado");
            }

            if (coordinador.getRole() != Role.COORDINADOR) {
                return ResponseEntity.badRequest().body("El usuario no es un coordinador");
            }

            int asignadosExitosamente = 0;
            for (Long asesorId : asignacionDTO.getAsesorIds()) {
                User asesor = userService.findUserById(asesorId);

                if (asesor != null && asesor.getRole() == Role.ASESOR) {
                    asesor.setCoordinador(coordinador);
                    userService.updateUser(asesorId, asesor);
                    asignadosExitosamente++;
                }
            }

            return ResponseEntity.ok("Se asignaron " + asignadosExitosamente + " asesores al coordinador");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al asignar asesores: " + e.getMessage());
        }
    }
}