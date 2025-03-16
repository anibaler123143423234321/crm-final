package com.midas.crm.controller;

import com.midas.crm.entity.DTO.ClientePromocionBody;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.exceptions.MidasExceptions;
import com.midas.crm.service.ClienteResidencialService;
import com.midas.crm.service.UserService;
import com.midas.crm.utils.GenericResponse;
import com.midas.crm.utils.GenericResponseConstants;
import com.midas.crm.utils.MidasErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientePromocionController {

    @Autowired
    private ClienteResidencialService clienteService;

    @Autowired
    private UserService userService;

    @PostMapping("/cliente-promocion")
    public ResponseEntity<GenericResponse<?>> crearClienteYPromocion(@RequestBody ClientePromocionBody body) {
        ClienteResidencial cliente = body.getClienteResidencial();
        Long usuarioId = body.getUsuarioId();

        if (cliente == null || usuarioId == null) {
            throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_INVALID_DATA);
        }

        try {
            ClienteResidencial clienteGuardado = clienteService.guardar(cliente, usuarioId);

            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Datos guardados con éxito");
            respuesta.put("cliente", clienteGuardado);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Cliente promoción creado exitosamente",
                            respuesta
                    ));
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }

    @GetMapping("/cliente-promocion")
    public ResponseEntity<GenericResponse<List<ClienteResidencial>>> listarClientesPromocion() {
        List<ClienteResidencial> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(
                new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Lista de clientes promoción obtenida exitosamente",
                        clientes
                )
        );
    }

    @GetMapping("/cliente-promocion/movil/{movil}")
    public ResponseEntity<GenericResponse<ClienteResidencial>> obtenerClientePromocionPorMovil(@PathVariable String movil) {
        List<ClienteResidencial> clientes = clienteService.buscarPorMovil(movil);
        if (clientes.isEmpty()) {
            throw new MidasExceptions(MidasErrorMessage.CLIENTERESIDENCIAL_NOT_FOUND);
        }

        // Retornar el primer registro encontrado
        return ResponseEntity.ok(
                new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Cliente promoción encontrado exitosamente",
                        clientes.get(0)
                )
        );
    }
}