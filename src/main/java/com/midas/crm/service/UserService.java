package com.midas.crm.service;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<User> findAllUsers(Pageable pageable);
    User saveUserIndividual(User user);
    User saveUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void changeRole(Role newRole, String username);
    User findByUsernameReturnToken(String username);
    User findUserById(Long userId);
    void saveUsers(List<User> users);
    void saveUsersBackOffice(List<User> users);
    User updateUser(Long userId, User updateUser);
    boolean deleteUser(Long userId);
    Page<User> searchAllFields(String query, Pageable pageable);
    List<User> findAllCoordinadores();
    List<User> findAsesoresSinCoordinador();
}