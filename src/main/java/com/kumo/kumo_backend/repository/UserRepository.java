package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // ✅ CORREGIDO: 'nombre' en lugar de 'names'
    List<User> findByNombreContainingIgnoreCase(String nombre);

    List<User> findByRol(String rol);

    List<User> findByActivoTrue();

    List<User> findByActivoFalse();

    boolean existsByEmail(String email);

    boolean existsByEmailAndActivoTrue(String email);
}