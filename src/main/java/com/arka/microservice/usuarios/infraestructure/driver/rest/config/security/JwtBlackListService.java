package com.arka.microservice.usuarios.infraestructure.driver.rest.config.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtBlackListService {
    // Almacenamos el token y su fecha de expiración (en milisegundos).
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    // Metodo para agregar el token a la blacklist.
    public void blacklistToken(String token, long expirationTime) {
        blacklist.put(token, expirationTime);
    }

    // Metodo para verificar si un token está en la blacklist y sigue vigente.
    public boolean isBlacklisted(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) {
            return false;
        }
        // Si el token ya expiró, se elimina de la blacklist.
        if (expiry < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
