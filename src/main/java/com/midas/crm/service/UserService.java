package com.midas.crm.service;


import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;


public interface UserService {
    Page<User> findAllUsers(Pageable pageable);

    User saveUserIndividual(User user);

    //List<User> findAllUsers();
    User saveUser(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    void changeRole(Role newRole, String username);

    User findByUsernameReturnToken(String username);

    User findUserById(Long userId);
    @Transactional
    void saveUsers(List<User> users);

    @Transactional
    void saveUsersBackOffice(List<User> users);

    User updateUser(Long id, User updateUser);


    // ============== Eliminar usuario ==================
    boolean deleteUser(Long userId);

    Page<User> searchAllFields(String query, Pageable pageable);
}

