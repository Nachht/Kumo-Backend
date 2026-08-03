package com.kumo.kumo_backend.repository;

import com.kumo.kumo_backend.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // ===== MÉTODOS PERSONALIZADOS =====

    // Buscar contactos por email
    List<Contact> findByEmail(String email);

    // Buscar contactos por rango de fechas
    List<Contact> findByFechaBetween(LocalDateTime start, LocalDateTime end);
}