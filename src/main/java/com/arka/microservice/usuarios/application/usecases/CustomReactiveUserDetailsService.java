package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.ports.out.AuthPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {
    private final AuthPersistencePort service;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return service.findByEmail(email)
                .map(userModel -> User.builder()
                        .username(userModel.getEmail())
                        // La contraseña ya debe estar encriptada
                        .password(userModel.getPassword())
                        // Convertimos el tipo de usuario a rol (por ejemplo, ADMIN o CLIENT)
                        .roles(userModel.getUserType().name())
                        .build()
                );
    }
}
