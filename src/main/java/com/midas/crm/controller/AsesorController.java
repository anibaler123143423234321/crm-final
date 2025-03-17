package com.midas.crm.controller;

import com.midas.crm.entity.DTO.AsesorDTO;
import com.midas.crm.entity.DTO.ClienteResidencialDTO;
import com.midas.crm.service.AsesorService;
import com.midas.crm.service.ClienteResidencialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asesores")
@RequiredArgsConstructor
@Slf4j
public class AsesorController {

    private final AsesorService asesorService;
    private final ClienteResidencialService clienteResidencialService;

    /**
     * Obtiene todos los asesores
     * @return Lista de asesores
     */
    @GetMapping
    public ResponseEntity<List<AsesorDTO>> getAllAsesores() {
        log.info("Obteniendo todos los asesores");
        List<AsesorDTO> asesores = asesorService.getAllAsesores();
        return ResponseEntity.ok(asesores);
    }

    /**
     * Obtiene un asesor por su ID
     * @param id ID del asesor
     * @return Asesor si existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsesorDTO> getAsesorById(@PathVariable Long id) {
        log.info("Obteniendo asesor con ID: {}", id);
        return asesorService.getAsesorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene los asesores asignados a un coordinador
     * @param coordinadorId ID del coordinador
     * @return Lista de asesores
     */
    @GetMapping("/por-coordinador/{coordinadorId}")
    public ResponseEntity<List<AsesorDTO>> getAsesoresByCoordinador(@PathVariable Long coordinadorId) {
        log.info("Obteniendo asesores asignados al coordinador con ID: {}", coordinadorId);
        List<AsesorDTO> asesores = asesorService.getAsesoresByCoordinadorId(coordinadorId);
        return ResponseEntity.ok(asesores);
    }

    /**
     * Obtiene los clientes residenciales asociados a un asesor
     * @param id ID del asesor
     * @return Lista de clientes residenciales
     */
    @GetMapping("/{id}/clientes")
    public ResponseEntity<List<ClienteResidencialDTO>> getClientesByAsesor(@PathVariable Long id) {
        log.info("Obteniendo clientes del asesor con ID: {}", id);
        // Verificar primero si el asesor existe
        if (asesorService.getAsesorById(id).isEmpty()) {
            log.warn("Asesor con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }

        List<ClienteResidencialDTO> clientes = clienteResidencialService.getClientesByAsesorId(id);
        log.info("Se encontraron {} clientes para el asesor con ID: {}", clientes.size(), id);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtiene estadísticas de clientes para un asesor
     * @param id ID del asesor
     * @return Estadísticas de clientes
     */
    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasByAsesor(@PathVariable Long id) {
        log.info("Obteniendo estadísticas para el asesor con ID: {}", id);
        // Verificar primero si el asesor existe
        if (asesorService.getAsesorById(id).isEmpty()) {
            log.warn("Asesor con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> estadisticas = new HashMap<>();
        Long totalClientes = clienteResidencialService.countClientesByAsesorId(id);
        List<ClienteResidencialDTO> ventasRealizadas = clienteResidencialService.getVentasRealizadasByAsesorId(id);

        estadisticas.put("totalClientes", totalClientes);
        estadisticas.put("ventasRealizadas", ventasRealizadas.size());
        estadisticas.put("porcentajeExito", totalClientes > 0 ?
                (double) ventasRealizadas.size() / totalClientes * 100 : 0);

        log.info("Estadísticas obtenidas para el asesor con ID {}: total={}, ventas={}",
                id, totalClientes, ventasRealizadas.size());

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene los clientes con venta realizada asociados a un asesor
     * @param id ID del asesor
     * @return Lista de clientes con venta realizada
     */
    @GetMapping("/{id}/ventas")
    public ResponseEntity<List<ClienteResidencialDTO>> getVentasByAsesor(@PathVariable Long id) {
        log.info("Obteniendo ventas realizadas por el asesor con ID: {}", id);
        // Verificar primero si el asesor existe
        if (asesorService.getAsesorById(id).isEmpty()) {
            log.warn("Asesor con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }

        List<ClienteResidencialDTO> ventas = clienteResidencialService.getVentasRealizadasByAsesorId(id);
        log.info("Se encontraron {} ventas realizadas para el asesor con ID: {}", ventas.size(), id);
        return ResponseEntity.ok(ventas);
    }
}