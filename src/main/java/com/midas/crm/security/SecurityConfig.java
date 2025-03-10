package com.midas.crm.security;

import com.midas.crm.security.jwt.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    @Lazy
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll() // Permite acceso a /error
                        .requestMatchers(
                                "/api/authentication/sign-in",
                                "/api/authentication/sign-up",
                                "/api/cliente-promocion",
                                "/api/user/crear-masivo",
                                "/api/user/crear-masivo-backoffice",
                                "/api/user/listar",
                                "/api/user/change/**",
                                "/api/user",
                                "/api/sms/send",
                                "/api/user/**",
                                "/api/user/buscar",
                                "/api/user/soft/**",
                                "/api/clientes/con-usuario",
                                "/api/clientes/con-usuario-filtrados",
                                "/api/clientes/exportar-excel-individual/{movil}",
                                "/api/clientes/exportar-excel-masivo",
                                "/api/cliente-promocion/movil/{movil}",
                                "/api/cliente-promocion/",
                                "/api/clientes/exportar-excel-individual/{movil}",
                                "/api/generateUsername",
                                "/api/registerMessagingToken",
                                "/",
                                "/index.html",
                                "/api/fcm/send"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // ✅ CONFIGURACIÓN CORRECTA DE CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of(
                    "http://localhost:5200",
                    "https://seguimiento-egresado.web.app",
                    "https://apisozarusac.com",
                    "https://apisozarusac.com/BackendJava",
                    "https://www.api.midassolutiongroup.com",
                    "https://project-a16f1.web.app",
                    "https://www.leads.midassolutiongroup.com"
            ));
            config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    // ✅ CONFIGURACIÓN DE FIREWALL PARA PERMITIR %0A
    @Bean
    public HttpFirewall httpFirewall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall(); // Usa el firewall predeterminado
        return firewall;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:5200",
                                "https://apisozarusac.com/BackendJava",
                                "https://seguimiento-egresado.web.app",
                                "https://apisozarusac.com",
                                "https://www.api.midassolutiongroup.com",
                                "https://project-a16f1.web.app",
                                "https://www.leads.midassolutiongroup.com"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true);
            }
        };
    }
}
