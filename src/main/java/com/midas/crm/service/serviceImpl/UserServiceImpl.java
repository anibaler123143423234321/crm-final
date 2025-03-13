package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.security.jwt.JwtProvider;
import com.midas.crm.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // Inicializa el usuario ADMIN si aún no existe
    private void initializeAdminUser() {
        try {
            if (!userRepository.existsByUsername("70680710") && !userRepository.existsByDni("70680710")) {
                User adminUser = new User();
                adminUser.setUsername("70680710");
                adminUser.setPassword(passwordEncoder.encode("$olutions2K25."));
                adminUser.setNombre("Andree");
                adminUser.setApellido("Admin");
                adminUser.setTelefono("123456789");
                adminUser.setSede("Chiclayo");
                adminUser.setDni("70680710");
                adminUser.setEmail("admin@midas.pe");
                adminUser.setFechaCreacion(LocalDateTime.now());
                adminUser.setRole(Role.ADMIN);
                adminUser.setEstado("A");
                userRepository.save(adminUser);
                log.info("Usuario ADMIN creado exitosamente.");
            } else {
                log.info("El usuario ADMIN ya existe.");
            }
        } catch (Exception e) {
            log.error("Error al inicializar el usuario ADMIN", e);
        }
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User saveUserIndividual(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        log.debug("Longitud de la contraseña cifrada: {}", encodedPassword.length());
        user.setPassword(encodedPassword);
        if (user.getRole() == null) {
            user.setRole(Role.ASESOR);
        }
        user.setEstado("A");
        user.setFechaCreacion(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        log.debug("Longitud de la contraseña cifrada: {}", encodedPassword.length());
        user.setPassword(encodedPassword);
        user.setRole(Role.ASESOR);
        user.setEstado("A");
        user.setFechaCreacion(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public void changeRole(Role newRole, String username) {
        userRepository.updateUserRole(username, newRole);
    }

    @Override
    public User findByUsernameReturnToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe: " + username));
        String jwt = jwtProvider.generateToken(UserPrincipal.build(user));
        user.setToken(jwt);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    @Override
    public void saveUsers(List<User> users) {
        users.forEach(user -> {
            if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByDni(user.getDni())) {
                return; // Salta si ya existe
            }
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(user.getUsername() + "@midas.pe");
            }
            user.setRole(Role.ASESOR);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEstado("A");
            user.setFechaCreacion(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Transactional
    @Override
    public void saveUsersBackOffice(List<User> users) {
        users.forEach(user -> {
            if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByDni(user.getDni())) {
                return;
            }
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(user.getUsername() + "@midas.pe");
            }
            user.setRole(Role.BACKOFFICE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEstado("A");
            user.setFechaCreacion(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Override
    public User updateUser(Long userId, User updateUser) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setNombre(updateUser.getNombre());
            existingUser.setApellido(updateUser.getApellido());
            existingUser.setUsername(updateUser.getUsername());
            existingUser.setSede(updateUser.getSede());
            existingUser.setTelefono(updateUser.getTelefono());
            existingUser.setEmail(updateUser.getEmail());
            existingUser.setEstado(updateUser.getEstado());
            return userRepository.save(existingUser);
        }).orElse(null);
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    @Override
    public Page<User> searchAllFields(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.searchAllFields(query, pageable);
    }

    private User updateUserAttributes(User existingUser, User updateUser) {
        if (updateUser.getNombre() != null) {
            existingUser.setNombre(updateUser.getNombre());
        }
        if (updateUser.getApellido() != null) {
            existingUser.setApellido(updateUser.getApellido());
        }
        if (updateUser.getUsername() != null) {
            existingUser.setUsername(updateUser.getUsername());
        }
        if (updateUser.getTelefono() != null) {
            existingUser.setTelefono(updateUser.getTelefono());
        }
        if (updateUser.getEmail() != null) {
            existingUser.setEmail(updateUser.getEmail());
        }
        if (updateUser.getTokenPassword() != null) {
            existingUser.setTokenPassword(updateUser.getTokenPassword());
        }
        return existingUser;
    }
}
