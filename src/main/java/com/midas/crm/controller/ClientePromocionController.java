package com.midas.crm.controller;

import com.midas.crm.entity.ClientePromocionBody;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.entity.User;
import com.midas.crm.service.ClienteResidencialService;
import com.midas.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientePromocionController {

    @Autowired
    private ClienteResidencialService clienteService;

    @Autowired
    private UserService userService;

    @PostMapping("/cliente-promocion")
    public ResponseEntity<?> crearClienteYPromocion(@RequestBody ClientePromocionBody body) {
        ClienteResidencial cliente = body.getClienteResidencial();
        Long usuarioId = body.getUsuarioId();

        if (cliente == null || usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Faltan datos de clienteResidencial o usuarioId");
        }

        ClienteResidencial clienteGuardado = clienteService.guardar(cliente, usuarioId);

        var respuesta = new java.util.HashMap<String, Object>();
        respuesta.put("mensaje", "Datos guardados con éxito");
        respuesta.put("cliente", clienteGuardado);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/cliente-promocion")
    public ResponseEntity<?> listarClientesPromocion() {
        List<ClienteResidencial> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/cliente-promocion/movil/{movil}")
    public ResponseEntity<ClienteResidencial> obtenerClientePromocionPorMovil(@PathVariable String movil) {
        List<ClienteResidencial> clientes = clienteService.buscarPorMovil(movil);
        if (clientes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con móvil: " + movil);
        }
        // Retornar el primer registro encontrado
        return ResponseEntity.ok(clientes.get(0));
    }

}