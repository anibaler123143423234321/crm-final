package com.midas.crm.controller;

import com.midas.crm.entity.User;
import com.midas.crm.exceptions.MidasExceptions;
import com.midas.crm.service.AuthenticationService;
import com.midas.crm.service.UserService;
import com.midas.crm.utils.GenericResponse;
import com.midas.crm.utils.GenericResponseConstants;
import com.midas.crm.utils.MidasErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("sign-up")
    public ResponseEntity<GenericResponse<?>> signUp(@RequestBody User user) {
        if(userService.findByUsername(user.getUsername()).isPresent()) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_ALREADY_EXISTS);
        }

        // Si no se envía email, generarlo a partir del username
        if(user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail(user.getUsername() + "@midas.pe");
        }

        // Verificar el email sólo si se envió o se generó
        if(userService.findByEmail(user.getEmail()).isPresent()) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_ALREADY_EXISTS);
        }

        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(
                        GenericResponseConstants.SUCCESS,
                        "Usuario creado exitosamente",
                        savedUser
                ));
    }

    @PostMapping("sign-in")
    public ResponseEntity<GenericResponse<?>> signIn(@RequestBody User user) {
        try {
            User authenticatedUser = authenticationService.signInAndReturnJWT(user);
            return ResponseEntity.ok(
                    new GenericResponse<>(
                            GenericResponseConstants.SUCCESS,
                            "Inicio de sesión exitoso",
                            authenticatedUser
                    )
            );
        } catch (BadCredentialsException e) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_INVALID_LOGIN);
        } catch (UsernameNotFoundException e) {
            throw new MidasExceptions(MidasErrorMessage.USUARIO_NOT_FOUND);
        } catch (Exception e) {
            throw new MidasExceptions(MidasErrorMessage.ERROR_INTERNAL);
        }
    }
}