package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.TokenResponse;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.security.jwt.JwtProvider;
import com.midas.crm.service.AuthenticationService;
import com.midas.crm.service.RefreshTokenService;
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

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public TokenResponse signInAndReturnJWT(User signInRequest) {
        // Buscar el usuario por username
        User user = userRepository.findByUsername(signInRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + signInRequest.getUsername()));

        // Autenticar usando username y password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtProvider.generateToken(userPrincipal);

        // Generar refresh token para el usuario
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        // Opcional: puedes asignar el token a la entidad si lo deseas
        user.setToken(jwt);

        // Construir la respuesta con todos los datos necesarios
        return new TokenResponse(
                jwt,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getNombre(),
                user.getApellido(),
                user.getDni(),
                user.getEmail(),
                user.getTelefono(),
                user.getRole().name(),
                user.getSede()
        );
    }

    @Override
    public TokenResponse refreshAccessToken(String refreshToken) {
        return null;
    }
}
