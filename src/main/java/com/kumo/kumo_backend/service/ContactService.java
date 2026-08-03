package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.Contact;
import com.kumo.kumo_backend.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // ===== LISTAR TODOS LOS MENSAJES =====
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    // ===== BUSCAR MENSAJE POR ID =====
    @Transactional(readOnly = true)
    public Contact findById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con ID: " + id));
    }

    // ===== GUARDAR MENSAJE DE CONTACTO =====
    @Transactional
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    // ===== ELIMINAR MENSAJE =====
    @Transactional
    public void deleteById(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Mensaje no encontrado con ID: " + id);
        }
        contactRepository.deleteById(id);
    }

    // ===== BUSCAR MENSAJES POR EMAIL =====
    @Transactional(readOnly = true)
    public List<Contact> findByEmail(String email) {
        return contactRepository.findByEmail(email);
    }
}