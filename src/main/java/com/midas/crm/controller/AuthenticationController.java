package com.midas.crm.controller;


import com.midas.crm.entity.TokenResponse;
import com.midas.crm.entity.User;
import com.midas.crm.service.AuthenticationService;
import com.midas.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        TokenResponse tokenResponse = authenticationService.signInAndReturnJWT(user);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }


    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            TokenResponse tokenResponse = authenticationService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token inválido o expirado");
        }
    }
}