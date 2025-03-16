package com.midas.crm.controller;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.exceptions.MidasExceptions;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.service.ExcelService;
import com.midas.crm.service.UserService;
import com.midas.crm.utils.GenericResponse;
import com.midas.crm.utils.GenericResponseConstants;
import com.midas.crm.utils.MidasErrorMessage;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/registrar")
    public ResponseEntity<GenericResponse<User>> registrarUsuario(@RequestBody User user) {
        try {
            // Llama a saveUserIndividual, que solo guarda el usuario sin manipular token o sesión
            User usuarioRegistrado = userService.saveUserIndividual(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Usuario registrado exitosamente",
                            usuarioRegistrado
                    )
            );
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_ALREADY_EXISTS);
        }
    }

    @PostMapping("crear-masivo-backoffice")
    public ResponseEntity<GenericResponse<String>> createBackofficeUsersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_INVALID_DATA);
        }

        try {
            List<User> users = excelService.leerUsuariosDesdeExcelBackoffice(file);
            userService.saveUsersBackOffice(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Usuarios BACKOFFICE cargados exitosamente",
                            "Proceso completado correctamente"
                    )
            );
        } catch (IOException e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }

    @PostMapping("crear-masivo")
    public ResponseEntity<GenericResponse<String>> createUsersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_INVALID_DATA);
        }

        try {
            List<User> users = excelService.leerUsuariosDesdeExcel(file);
            userService.saveUsers(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Usuarios cargados exitosamente",
                            "Proceso completado correctamente"
                    )
            );
        } catch (IOException e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }

    @GetMapping("listar")
    public ResponseEntity<GenericResponse<Map<String, Object>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<User> pageUsers = userService.findAllUsers(paging);

            Map<String, Object> response = new HashMap<>();
            response.put("users", pageUsers.getContent());
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Lista de usuarios obtenida exitosamente",
                            response
                    )
            );
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }

    @PutMapping("change/{role}/{userId}")
    public ResponseEntity<GenericResponse<String>> changeRole(
            @PathVariable Role role,
            @PathVariable Long userId) {

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
            }

            // Si el usuario ya tiene el rol solicitado
            if (user.getRole() == role) {
                return ResponseEntity.ok(
                        new GenericResponse<>(
                                GenericResponseConstants.SUCCESS,
                                "El usuario ya tiene el rol " + role,
                                "No se requieren cambios"
                        )
                );
            }

            // Si estamos cambiando a un asesor a coordinador y tiene un coordinador asignado
            if (role == Role.COORDINADOR && user.getRole() == Role.ASESOR && user.getCoordinador() != null) {
                // Desasignar el coordinador actual
                user.setCoordinador(null);
                userService.updateUser(userId, user);
            }

            // Si estamos cambiando un coordinador a otro rol
            if (user.getRole() == Role.COORDINADOR && role != Role.COORDINADOR) {
                // Verificar si tiene asesores asignados
                if (user.getAsesores() != null && !user.getAsesores().isEmpty()) {
                    // Desasignar todos los asesores
                    for (User asesor : user.getAsesores()) {
                        asesor.setCoordinador(null);
                        userService.updateUser(asesor.getId(), asesor);
                    }
                }
            }

            // Cambiar el rol
            userService.changeRole(role, user.getUsername());
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Rol actualizado correctamente a " + role,
                            "Cambio de rol completado"
                    )
            );
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<GenericResponse<User>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsernameReturnToken(userPrincipal.getUsername());
        return ResponseEntity.ok(
                new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Usuario actual obtenido exitosamente",
                        user
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GenericResponse<User>> getUsuarioById(@PathVariable Long userId) {
        try {
            User usuario = userService.findUserById(userId);
            if (usuario != null) {
                return ResponseEntity.ok(
                        new GenericResponse<>(
                                GenericResponseConstants.SUCCESS,
                                "Usuario encontrado exitosamente",
                                usuario
                        )
                );
            } else {
                throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
            }
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
            }
            throw e;
        }
    }

    @DeleteMapping("/soft/{userId}")
    public ResponseEntity<GenericResponse<Void>> deleteUser(@PathVariable Long userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Usuario eliminado exitosamente",
                            null
                    )
            );
        } else {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<GenericResponse<Map<String, Object>>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<User> pageUsers = userService.searchAllFields(query, paging);

            Map<String, Object> response = new HashMap<>();
            response.put("users", pageUsers.getContent());
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Búsqueda de usuarios completada exitosamente",
                            response
                    )
            );
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<GenericResponse<User>> updateUser(
            @PathVariable Long userId,
            @RequestBody User updateUser) {
        User updatedUser = userService.updateUser(userId, updateUser);
        if (updatedUser != null) {
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Usuario actualizado exitosamente",
                            updatedUser
                    )
            );
        } else {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
        }
    }

    @PutMapping("/asignar-coordinador/{asesorId}/{coordinadorId}")
    public ResponseEntity<GenericResponse<String>> asignarCoordinadorAUsuario(
            @PathVariable Long asesorId,
            @PathVariable Long coordinadorId) {
        try {
            User asesor = userService.findUserById(asesorId);
            User coordinador = userService.findUserById(coordinadorId);

            if (asesor == null) {
                throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
            }

            if (coordinador == null) {
                throw new MidasExceptions(MidasErrorMessage.COORDINADOR_NOT_FOUND);
            }

            if (asesor.getRole() != Role.ASESOR) {
                throw new MidasExceptions(MidasErrorMessage.ASIGNACION_ASESOR_FAILED);
            }

            if (coordinador.getRole() != Role.COORDINADOR) {
                throw new MidasExceptions(MidasErrorMessage.ASIGNAR_COORDINADOR_FAILED);
            }

            asesor.setCoordinador(coordinador);
            userService.updateUser(asesorId, asesor);

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Coordinador asignado correctamente",
                            "Asignación completada exitosamente"
                    )
            );
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ASIGNAR_COORDINADOR_FAILED);
            }
            throw e;
        }
    }

    @DeleteMapping("/remover-coordinador/{asesorId}")
    public ResponseEntity<GenericResponse<String>> removerCoordinadorDeUsuario(@PathVariable Long asesorId) {
        try {
            User asesor = userService.findUserById(asesorId);

            if (asesor == null) {
                throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
            }

            if (asesor.getRole() != Role.ASESOR) {
                throw new MidasExceptions(MidasErrorMessage.ASIGNACION_ASESOR_FAILED);
            }

            if (asesor.getCoordinador() == null) {
                throw new MidasExceptions(MidasErrorMessage.ASIGNAR_COORDINADOR_FAILED);
            }

            asesor.setCoordinador(null);
            userService.updateUser(asesorId, asesor);

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Coordinador removido correctamente",
                            "Remoción completada exitosamente"
                    )
            );
        } catch (Exception e) {
            if (!(e instanceof MidasExceptions)) {
                throw new MidasExceptions(MidasErrorMessage.ASIGNAR_COORDINADOR_FAILED);
            }
            throw e;
        }
    }

    @GetMapping("/coordinadores")
    public ResponseEntity<GenericResponse<List<User>>> obtenerCoordinadores() {
        try {
            List<User> coordinadores = userService.findAllCoordinadores();
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Lista de coordinadores obtenida exitosamente",
                            coordinadores
                    )
            );
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }

    @GetMapping("/asesores-sin-coordinador")
    public ResponseEntity<GenericResponse<List<User>>> obtenerAsesoresSinCoordinador() {
        try {
            List<User> asesores = userService.findAsesoresSinCoordinador();
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Lista de asesores sin coordinador obtenida exitosamente",
                            asesores
                    )
            );
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }
}