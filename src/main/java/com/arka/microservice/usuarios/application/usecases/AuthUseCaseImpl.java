package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.ports.in.IAuthPortUseCase;
import com.arka.microservice.usuarios.domain.ports.out.UserPersistencePort;
import com.arka.microservice.usuarios.infraestructure.config.utils.DomainUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Clase usada para implementar la logica de negocio sobre cada funcion
 */
/*@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements IAuthPortUseCase{
    private final UserPersistencePort servicePort;
    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authenticationManager;

    @Override
    public Mono<AuthModel> authenticateUser(UserModel user) {
        // Crea un token de autenticación con las credenciales del usuario
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        // Procesa la autenticación de forma reactiva
        return authenticationManager.authenticate(authToken)
                .flatMap(authentication -> {
                    // Una vez autenticado, se obtiene el objeto principal
                    DomainUserDetails userDetails = (DomainUserDetails) authentication.getPrincipal();

                    // Genera el JWT utilizando tu servicio
                    *//*String jwtToken = jwtService.generateToken(userDetails);*//*

                    // Crea y retorna la respuesta de autenticación con el token
                    AuthModel authModel = new AuthModel();
                    *//*authModel.setAccessToken(jwtToken);*//*
                    return Mono.just(authModel);
                });
    }
}*/
