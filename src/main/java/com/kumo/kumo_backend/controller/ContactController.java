package com.kumo.kumo_backend.controller;

import com.kumo.kumo_backend.model.Contact;
import com.kumo.kumo_backend.service.ContactService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    // ✅ PÚBLICO - Cualquier visitante puede enviar mensaje
    @PostMapping
    public ResponseEntity<Contact> sendMessage(@RequestBody Contact contact) {
        Contact saved = contactService.save(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 🔒 ADMIN - Solo administradores pueden ver los mensajes
    @GetMapping
    public ResponseEntity<List<Contact>> getAllMessages() {
        return ResponseEntity.ok(contactService.findAll());
    }

    // 🔒 ADMIN - Solo administradores pueden ver un mensaje específico
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.findById(id));
    }

    // 🔒 ADMIN - Solo administradores pueden eliminar mensajes
    @DeleteMapping("/{id}")
    @Hidden
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        contactService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}