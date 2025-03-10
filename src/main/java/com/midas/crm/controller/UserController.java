package com.midas.crm.controller;


import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.service.ExcelService;
import com.midas.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ExcelService excelService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("crear-masivo-backoffice")
    public ResponseEntity<?> createBackofficeUsersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Debe subir un archivo Excel válido.");
        }

        try {
            List<User> users = excelService.leerUsuariosDesdeExcelBackoffice(file);
            userService.saveUsers(users); // Aquí se asume que saveUsers maneja la creación de usuarios.
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuarios BACKOFFICE cargados exitosamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo Excel: " + e.getMessage());
        }
    }


    @PostMapping("crear-masivo")
    public ResponseEntity<?> createUsersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Debe subir un archivo Excel válido.");
        }

        try {
            List<User> users = excelService.leerUsuariosDesdeExcel(file);
            userService.saveUsers(users); // Aquí ya se genera automáticamente el email si no está
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuarios cargados exitosamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo Excel: " + e.getMessage());
        }
    }

    /*
    @GetMapping("listar")
    public List<User> listUsers() {
        return userService.findAllUsers();
    }
*/

    @GetMapping("listar")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            // Creamos un Pageable. Opcionalmente, podrías añadir un Sort.
            Pageable paging = PageRequest.of(page, size);

            // Llamamos a nuestro servicio para obtener la página
            Page<User> pageUsers = userService.findAllUsers(paging);

            // Armamos la respuesta en un Map para mayor claridad
            Map<String, Object> response = new HashMap<>();
            response.put("users", pageUsers.getContent());
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("change/{role}/{userId}")
    public ResponseEntity<?> changeRole(
            @PathVariable Role role,
            @PathVariable Long userId) {

        User user = userService.findUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        // Lógica adicional si fuera necesario. Por ejemplo, si quieres hacer algo distinto
        // en caso de que el usuario ya tenga cierto rol.
        userService.changeRole(role, user.getUsername());
        return ResponseEntity.ok("Rol actualizado correctamente.");
    }



    @GetMapping
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        return new ResponseEntity<>(userService.findByUsernameReturnToken(userPrincipal.getUsername()), HttpStatus.OK);
    }

    // http://locahost:5555/gateway/usuario/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long userId) {
        try {
            User usuario = userService.findUserById(userId);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============== NUEVO: Eliminar usuario ==================
// Endpoint para soft delete: cambia estado a inactivo en lugar de eliminar físicamente
    @DeleteMapping("/soft/{userId}")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long userId) {
        boolean inactivated = userService.softDeleteUser(userId);
        if (inactivated) {
            return ResponseEntity.ok("Usuario inactivado exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }


    // ============== NUEVO: Buscar usuario(s) por DNI (username) ==================
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<User> pageUsers = userService.searchAllFields(query, paging);

            Map<String, Object> response = new HashMap<>();
            response.put("users", pageUsers.getContent());
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para actualizar un usuario (sobrescribe los campos enviados)
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User updateUser) {
        User updatedUser = userService.updateUser(userId, updateUser);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }

}
