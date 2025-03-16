package com.midas.crm.controller;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.exceptions.MidasExceptions;
import com.midas.crm.service.ClienteResidencialExcelService;
import com.midas.crm.service.ClienteResidencialService;
import com.midas.crm.utils.GenericResponse;
import com.midas.crm.utils.GenericResponseConstants;
import com.midas.crm.utils.MidasErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
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
    public ResponseEntity<GenericResponse<Map<String, Object>>> obtenerClientesConUsuario(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<ClienteConUsuarioDTO> pageClientes = clienteResidencialService.obtenerClientesConUsuario(paging);

        Map<String, Object> response = new HashMap<>();
        response.put("clientes", pageClientes.getContent());
        response.put("currentPage", pageClientes.getNumber());
        response.put("totalItems", pageClientes.getTotalElements());
        response.put("totalPages", pageClientes.getTotalPages());

        return ResponseEntity.ok(
                new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Clientes con usuario obtenidos correctamente",
                        response
                )
        );
    }

    @GetMapping("/con-usuario-filtrados-fecha")
    public ResponseEntity<GenericResponse<Map<String, Object>>> obtenerClientesConUsuarioFiltradosPorFechaActual(
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

        return ResponseEntity.ok(
                new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Clientes filtrados por fecha actual obtenidos correctamente",
                        response
                )
        );
    }

    @GetMapping("/con-usuario-filtrados")
    public ResponseEntity<GenericResponse<Map<String, Object>>> obtenerClientesConUsuarioFiltrados(
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
                throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_INVALID_DATA);
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

        return ResponseEntity.ok(
                new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Clientes filtrados obtenidos correctamente",
                        response
                )
        );
    }

    /**
     * Endpoint para exportar en formato MASIVO (todos los clientes en una sola hoja).
     */
    @GetMapping("/exportar-excel-masivo")
    public ResponseEntity<byte[]> exportarExcelMasivo() {
        try {
            byte[] excelData = clienteResidencialExcelService.generarExcelClientesMasivo();

            if (excelData == null || excelData.length == 0) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes_residenciales_masivo.xlsx");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }
            throw e;
        }
    }

    /**
     * Endpoint para exportar en formato INDIVIDUAL (un solo cliente).
     * Se busca el cliente por su "movilContacto".
     */
    @GetMapping("/exportar-excel-individual/{movil}")
    public ResponseEntity<byte[]> exportarExcelIndividual(@PathVariable String movil) {
        try {
            byte[] excelData = clienteResidencialExcelService.generarExcelClienteIndividual(movil);

            if (excelData == null || excelData.length == 0) {
                throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cliente_residencial_" + movil + ".xlsx");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<ClienteResidencial>> actualizarCliente(
            @PathVariable Long id,
            @RequestBody ClienteResidencial cliente) {
        try {
            ClienteResidencial clienteActualizado = clienteResidencialService.actualizar(id, cliente);
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Cliente actualizado correctamente",
                            clienteActualizado
                    )
            );
        } catch (NoSuchElementException e) {
            throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_NOT_FOUND);
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }
            throw e;
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
            throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_INVALID_DATA);
        }

        try {
            byte[] excelData = clienteResidencialExcelService.generarExcelClientesPorFecha(fecha);
            if (excelData == null || excelData.length == 0) {
                throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_NOT_FOUND);
            }

            // Para el nombre del archivo usamos el mismo formato ISO (por ejemplo, "2025-03-13")
            String fechaArchivo = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes_residenciales_" + fechaArchivo + ".xlsx");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }
            throw e;
        }
    }
}