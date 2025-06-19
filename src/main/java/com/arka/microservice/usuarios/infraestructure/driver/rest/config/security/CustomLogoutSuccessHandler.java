package com.arka.microservice.usuarios.infraestructure.driver.rest.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomLogoutSuccessHandler implements ServerLogoutHandler {

    private final JwtBlackListService blackListService;
    private final JwtUtil jwtUtil; // Suponemos que JwtUtil tiene un metodo getExpirationFromToken()

    public CustomLogoutSuccessHandler(JwtBlackListService blackListService, JwtUtil jwtUtil) {
        this.blackListService = blackListService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * @param exchange
     * @param authentication
     * @return
     */
    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        // Obtenemos el ServerWebExchange del WebFilterExchange.
        ServerWebExchange webExchange = exchange.getExchange();

        // Extraemos el token del header Authorization.
        String authHeader = webExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Obtenemos la fecha de expiración del token (en milisegundos).
            long expiration = jwtUtil.getExpirationFromToken(token);
            // Agregamos el token a la blacklist.
            blackListService.blacklistToken(token, expiration);
        }
        // Configuramos la respuesta con status OK y completamos la respuesta.
        webExchange.getResponse().setStatusCode(HttpStatus.OK);
        return webExchange.getResponse().setComplete();
    }
}
