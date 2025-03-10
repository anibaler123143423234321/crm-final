package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.security.jwt.JwtProvider;
import com.midas.crm.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        // Inicializar el usuario ADMIN
        initializeAdminUser();
    }

    /*@Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    */

    // Método para inicializar el usuario ADMIN
    private void initializeAdminUser() {
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
            System.out.println("Usuario ADMIN creado exitosamente.");
        } else {
            System.out.println("El usuario ADMIN ya existe.");
        }
    }


    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User saveUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Longitud de la contraseña cifrada: " + encodedPassword.length());
        user.setPassword(encodedPassword);
        user.setRole(Role.ASESOR);
        user.setEstado("A"); // Estado "A" de Activo
        user.setFechaCreacion(LocalDateTime.now());
        return userRepository.save(user);
    }


    @Override
    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }
    @Override
    public Optional<User> findByEmail(String email) {return userRepository.findByEmail(email);}

    @Transactional
    @Override
    public void changeRole(Role newRole, String username)
    {
        userRepository.updateUserRole(username, newRole);
    }

    @Override
    public User findByUsernameReturnToken(String username)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe:" + username));

        String jwt = jwtProvider.generateToken(user);
        user.setToken(jwt);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public void saveUsers(List<User> users) {
        for (User user : users) {
            // Validar si el usuario ya existe por username o por DNI
            if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByDni(user.getDni())) {
                continue; // Si el usuario o el DNI ya existen, saltar
            }

            // Generar el email automáticamente si no está presente
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(user.getUsername() + "@midas.pe");
            }

            user.setRole(Role.ASESOR); // Asignar rol por defecto
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encriptar la contraseña
            user.setEstado("A"); // Estado activo
            user.setFechaCreacion(LocalDateTime.now()); // Fecha de creación actual
            userRepository.save(user);
        }
    }


    public User updateUser(Long userId, User updateUser) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if(existingUserOpt.isPresent()){
            User existingUser = existingUserOpt.get();
            // Actualiza los campos que deseas modificar
            existingUser.setNombre(updateUser.getNombre());
            existingUser.setApellido(updateUser.getApellido());
            existingUser.setUsername(updateUser.getUsername());
            existingUser.setSede(updateUser.getSede());
            existingUser.setTelefono(updateUser.getTelefono());
            existingUser.setEmail(updateUser.getEmail());
            // Asegúrate de actualizar también el estado (Activo/Inactivo)
            existingUser.setEstado(updateUser.getEstado());
            // ... Actualiza otros campos si es necesario

            return userRepository.save(existingUser);
        }
        return null;
    }


    // ============== Eliminar usuario ==================
    @Override
    public boolean softDeleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Cambiamos el estado a "I" (inactivo) y actualizamos la fecha de eliminación
            user.setEstado("I");
            user.setDeletionTime(LocalDateTime.now());
            userRepository.save(user);
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