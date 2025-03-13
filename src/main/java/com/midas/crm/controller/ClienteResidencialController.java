package com.midas.crm.controller;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.service.ClienteResidencialExcelService;
import com.midas.crm.service.ClienteResidencialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/clientes")
public class ClienteResidencialController {

    @Autowired
    private ClienteResidencialService clienteResidencialService;

    @Autowired
    private ClienteResidencialExcelService clienteResidencialExcelService;

    // ✅ Endpoint para obtener todos los clientes con usuario (sin filtros)
    @GetMapping("/con-usuario")
    public ResponseEntity<Map<String, Object>> obtenerClientesConUsuario(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<ClienteConUsuarioDTO> pageClientes = clienteResidencialService.obtenerClientesConUsuario(paging);

        Map<String, Object> response = new HashMap<>();
        response.put("clientes", pageClientes.getContent());
        response.put("currentPage", pageClientes.getNumber());
        response.put("totalItems", pageClientes.getTotalElements());
        response.put("totalPages", pageClientes.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/con-usuario-filtrados-fecha")
    public ResponseEntity<Map<String, Object>> obtenerClientesConUsuarioFiltradosPorFechaActual(
            @RequestParam(required = false) String dniAsesor,
            @RequestParam(required = false) String nombreAsesor,
            @RequestParam(required = false) String numeroMovil,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<ClienteConUsuarioDTO> pageClientes = clienteResidencialService
                .obtenerClientesConUsuarioFiltradosPorFechaActual(dniAsesor, nombreAsesor, numeroMovil, paging);

        Map<String, Object> response = new HashMap<>();
        response.put("clientes", pageClientes.getContent());
        response.put("currentPage", pageClientes.getNumber());
        response.put("totalItems", pageClientes.getTotalElements());
        response.put("totalPages", pageClientes.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/con-usuario-filtrados")
    public ResponseEntity<Map<String, Object>> obtenerClientesConUsuarioFiltrados(
            @RequestParam(required = false) String dniAsesor,
            @RequestParam(required = false) String nombreAsesor,
            @RequestParam(required = false) String numeroMovil,
            @RequestParam(required = false) String fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Convertir la cadena de fecha a LocalDate si se envía
        LocalDate fechaLocalDate = null;
        if (fecha != null && !fecha.isEmpty()) {
            try {
                fechaLocalDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de fecha inválido. Use el formato yyyy-MM-dd.");
            }
        }

        Pageable paging = PageRequest.of(page, size);
        Page<ClienteConUsuarioDTO> pageClientes = clienteResidencialService.obtenerClientesConUsuarioFiltrados(
                dniAsesor, nombreAsesor, numeroMovil, fechaLocalDate, paging);

        Map<String, Object> response = new HashMap<>();
        response.put("clientes", pageClientes.getContent());
        response.put("currentPage", pageClientes.getNumber());
        response.put("totalItems", pageClientes.getTotalElements());
        response.put("totalPages", pageClientes.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint para exportar en formato MASIVO (todos los clientes en una sola hoja).
     */
    @GetMapping("/exportar-excel-masivo")
    public ResponseEntity<byte[]> exportarExcelMasivo() {
        byte[] excelData = clienteResidencialExcelService.generarExcelClientesMasivo();

        if (excelData == null || excelData.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes_residenciales_masivo.xlsx");
        headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    /**
     * Endpoint para exportar en formato INDIVIDUAL (un solo cliente).
     * Se busca el cliente por su "movilContacto".
     */
    @GetMapping("/exportar-excel-individual/{movil}")
    public ResponseEntity<byte[]> exportarExcelIndividual(@PathVariable String movil) {
        byte[] excelData = clienteResidencialExcelService.generarExcelClienteIndividual(movil);

        if (excelData == null || excelData.length == 0) {
            // Si no se encontró el cliente o el Excel quedó vacío
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cliente_residencial_" + movil + ".xlsx");
        headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id,
                                               @RequestBody ClienteResidencial cliente) {
        try {
            ClienteResidencial clienteActualizado = clienteResidencialService.actualizar(id, cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado con id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el cliente");
        }
    }

    /**
     * Endpoint para exportar a Excel los clientes filtrados por una fecha dada.
     * Ejemplo: /api/clientes/exportar-excel-por-fecha?fecha=2025-03-13
     */
    @GetMapping("/exportar-excel-por-fecha")
    public ResponseEntity<byte[]> exportarExcelPorFecha(@RequestParam("fecha") String fechaStr) {
        LocalDate fecha;
        try {
            // Se espera que la fecha esté en formato "yyyy-MM-dd"
            fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null);
        }

        byte[] excelData = clienteResidencialExcelService.generarExcelClientesPorFecha(fecha);
        if (excelData == null || excelData.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // Para el nombre del archivo usamos el mismo formato ISO (por ejemplo, "2025-03-13")
        String fechaArchivo = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes_residenciales_" + fechaArchivo + ".xlsx");
        headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }



}