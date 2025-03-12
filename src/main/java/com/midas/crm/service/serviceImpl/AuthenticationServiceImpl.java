package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.security.jwt.JwtProvider;
import com.midas.crm.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User signInAndReturnJWT(User signInRequest) {
        // Buscar el usuario por username
        User user = userRepository.findByUsername(signInRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + signInRequest.getUsername()));

        // Autenticar usando el username y password (no el email)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtProvider.generateToken(userPrincipal);

        // Guardar el token en la entidad User si quieres retornarlo
        User signInUser = userPrincipal.getUser();
        signInUser.setToken(jwt);

        return signInUser;
    }



}