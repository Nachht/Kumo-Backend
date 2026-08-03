package com.kumo.kumo_backend.repository;

// ❌ ELIMINA ESTE IMPORT
// import com.kumo.kumo_backend.enums.Role;

import com.kumo.kumo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByNamesContainingIgnoreCase(String names);

    // 🔥 CAMBIADO: de Role a String
    List<User> findByRol(String rol);

    List<User> findByActivoTrue();

    List<User> findByActivoFalse();

    boolean existsByEmail(String email);

    boolean existsByEmailAndActivoTrue(String email);
}