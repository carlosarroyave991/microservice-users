package com.arka.microservice.usuarios.infraestructure.driver.rest.config.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {
    // Lista blanca de rutas: todas las rutas bajo /api/v1/auth/** serán públicas.
    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**"};
    private static final String[] WHITE_LIST_OPENAPI = {"/swagger", "/swagger/**", "/api-docs", "/api-docs/**", "/webjars/**"};

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                // Agregamos el filtro JWT para manejar la validación del token en cada request.
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(WHITE_LIST_URL).permitAll()
                        .pathMatchers(WHITE_LIST_OPENAPI).permitAll()
                        .pathMatchers("/logout").permitAll()
                        // Específicamente: para eliminar direcciones, permitir tanto a CLIENT como a ADMIN
                        .pathMatchers(HttpMethod.DELETE, "/api/users/{userId}/addresses/{addressId}").hasAnyRole("client", "admin")
                        // Para eliminar usuarios, solo los ADMIN pueden.
                        .pathMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("admin").pathMatchers(HttpMethod.GET, "/api/users").hasRole("admin")
                        .pathMatchers(HttpMethod.GET, "/api/addresses/all").hasRole("admin")
                        .pathMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("client", "admin")
                        // Explicito para POST: lo permiten clientes y administradores.
                        .pathMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("client", "admin")
                        .pathMatchers(HttpMethod.POST, "/api/address").hasRole("admin")
                        .anyExchange().authenticated()
                )
                .logout(logout -> logout
                        // Configuramos la URL de logout
                        .logoutUrl("/logout")
                        // Handler simple que responde OK.
                        .logoutSuccessHandler(customLogoutSuccessHandler::logout)
                )
                .build();
    }
}
