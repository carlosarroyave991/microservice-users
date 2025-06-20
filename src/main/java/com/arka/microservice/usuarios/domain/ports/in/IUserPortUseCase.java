package com.arka.microservice.usuarios.domain.ports.in;

import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Se definen las operaciones REST que pueden utilizar para interactuar
 * con el nucreo del sistema.
 */
public interface IUserPortUseCase {
    // Retorna 0 o 1 usuario
    Mono<UserModel> getUserById(Long id);

    // Retorna 0 o más usuarios que coincidan con el nombre
    Flux<UserModel> getUsersByName(String name);

    // Devuelve todos los usuarios como un flujo reactivo
    Flux<UserModel> getAllUsers();

    // Crea un usuario y retorna el usuario creado
    /*Mono<UserModel> createUser(UserModel user);*/

    // Actualiza un usuario y retorna el usuario actualizado
    Mono<UserModel> updateUser(UserModel user, Long id);

    // Elimina un usuario y retorna una señal de completado
    Mono<Void> deleteUser(Long id);
}
