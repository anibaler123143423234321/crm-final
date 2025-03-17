package com.midas.crm.repository;

import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User set role=:role where username=:username")
    void updateUserRole(@Param("username") String username, @Param("role") Role role);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username)  LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.nombre)    LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.apellido)  LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.email)     LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.telefono)  LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.dni)       LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.sede)      LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    Page<User> searchAllFields(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<User> findByRole(Role role);


    Optional<User> findByIdAndRole(Long id, Role role);

    List<User> findByCoordinadorIdAndRole(Long coordinadorId, Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.coordinador IS NULL")
    List<User> findByRoleAndCoordinadorIsNull(@Param("role") Role role);

    boolean existsByDni(String dni); // Validar si ya existe un DNI

}

