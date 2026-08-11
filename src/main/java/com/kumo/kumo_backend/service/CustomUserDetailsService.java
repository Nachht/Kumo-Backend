package com.kumo.kumo_backend.service;

import com.kumo.kumo_backend.model.User;
import com.kumo.kumo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🔥 BUSCAR EL USUARIO EN LA BASE DE DATOS
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        System.out.println("🔍 CustomUserDetailsService - Usuario encontrado:");
        System.out.println("   Email: " + user.getEmail());
        System.out.println("   ID: " + user.getId());
        System.out.println("   Rol: " + user.getRol());

        // 🔥 DEVOLVER LA ENTIDAD USER DIRECTAMENTE (implementa UserDetails)
        return user;
    }
}