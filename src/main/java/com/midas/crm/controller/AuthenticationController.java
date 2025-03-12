package com.midas.crm.controller;


import com.midas.crm.entity.User;
import com.midas.crm.service.AuthenticationService;
import com.midas.crm.service.UserService;
import com.midas.crm.utils.ErrorResponse;
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
    public ResponseEntity<?> signUp(@RequestBody User user) {
        if(userService.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        }

        // Si no se envía email, generarlo a partir del username
        if(user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail(user.getUsername() + "@midas.pe");
        }
        // Verificar el email sólo si se envió o se generó
        if(userService.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }


    @PostMapping("sign-in")
    public ResponseEntity<?> signIn(@RequestBody User user) {
        try {
            User authenticatedUser = authenticationService.signInAndReturnJWT(user);
            return new ResponseEntity<>(authenticatedUser, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenciales incorrectas: usuario o contraseña inválidos", "CREDENCIALES_INVALIDAS"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Usuario no encontrado", "USUARIO_NO_ENCONTRADO"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Error de autenticación", "ERROR_AUTENTICACION"));
        }
    }


}