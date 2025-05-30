package com.arka.microservice.usuarios.domain.ports.out;

import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Se encarga de definir las dependencias externas que el nucleo necesita.
 */
public interface AuthPersistencePort {
    /*Mono<String> authenticate(AuthModel model);*/
    Mono<UserModel> findByEmail(String username);
    Mono<UserModel> register(UserModel model);
}
