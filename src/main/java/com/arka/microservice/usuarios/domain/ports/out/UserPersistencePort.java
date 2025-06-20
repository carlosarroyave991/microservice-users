package com.arka.microservice.usuarios.domain.ports.out;

import com.arka.microservice.usuarios.domain.models.UserModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Se encarga de definir las dependencias externas que el nucleo necesita.
 */
public interface UserPersistencePort {
    Mono<UserModel> findById(Long id);
    Mono<UserModel> findByDni(String dni);
    Flux<UserModel> findByName(String name);
    //Mono<UserModel> findByEmail(String email);
    Flux<UserModel> findAll();
   /* Mono<UserModel> save(UserModel user);*/
    Mono<UserModel> update(UserModel user);
    Mono<Void> deleteById(Long id);
}
