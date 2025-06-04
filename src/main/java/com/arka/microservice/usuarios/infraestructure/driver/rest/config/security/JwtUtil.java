package com.arka.microservice.usuarios.infraestructure.driver.rest.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    // Se inyectan los valores establecidos en application.properties
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpirationMs;

    // Si bien se inyecta el refresh-token, en este ejemplo no se utiliza de forma directa.
    @Value("${application.security.jwt.refresh-token}")
    private Long jwtRefreshToken;

    // Genera el token incluyendo también los roles del usuario.
    public String generateToken(UserDetails userDetails) {
        // Ejemplo: se espera que userDetails.getAuthorities() retorne los roles asignados.
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles) // Incluimos los roles en el token.
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public long getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().getTime();
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch(Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    // Se encarga de construir el Authentication incluyendo los roles extraídos del token.
    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject();

        // Extraemos los roles. Se espera que en el token se haya almacenado una lista de roles.
        List<String> roles = claims.get("roles", List.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            authorities = roles.stream()
                    .map(role -> {
                        // Si el rol ya comienza con "ROLE_", lo dejamos tal cual.
                        if (role.startsWith("ROLE_")) {
                            return new SimpleGrantedAuthority(role);
                        } else {
                            return new SimpleGrantedAuthority("ROLE_" + role);
                        }
                    })
                    .collect(Collectors.toList());
        }

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
