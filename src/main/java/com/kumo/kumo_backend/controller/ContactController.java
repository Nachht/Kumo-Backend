package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Contact;
import com.kumo.kumo_backend.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // ===== GET /api/contact =====
    // Listar todos los mensajes
    @GetMapping
    public ResponseEntity<List<Contact>> getAllMessages() {
        return ResponseEntity.ok(contactService.findAll());
    }

    // ===== GET /api/contact/{id} =====
    // Obtener mensaje por ID
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.findById(id));
    }

    // ===== GET /api/contact/email/{email} =====
    // Obtener mensajes por email
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Contact>> getMessagesByEmail(@PathVariable String email) {
        return ResponseEntity.ok(contactService.findByEmail(email));
    }

    // ===== POST /api/contact =====
    // Enviar mensaje de contacto
    @PostMapping
    public ResponseEntity<Contact> sendMessage(@RequestBody Contact contact) {
        Contact savedContact = contactService.save(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContact);
    }

    // ===== DELETE /api/contact/{id} =====
    // Eliminar mensaje
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        contactService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}