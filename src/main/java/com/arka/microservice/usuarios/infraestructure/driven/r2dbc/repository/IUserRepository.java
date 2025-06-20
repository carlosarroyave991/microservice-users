package com.arka.microservice.usuarios.infraestructure.driven.r2dbc.repository;

import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Se ocupa de las interacciones con la base de datos u otra fuente de datos,
 * generalmente utilizando entidades JPA u otro mapeo relacionado con la base de datos.
 * Puede no necesitar todos los métodos definidos en el puerto.
 */
public interface IUserRepository extends ReactiveCrudRepository<UserEntity, Long> {
    Mono<UserEntity> findById(Long id);
    Mono<UserEntity> findByDni(String dni);
    Mono<UserEntity> findByEmail(String email);

    //@Query("SELECT u FROM UserEntity u WHERE u.name LIKE CONCAT('%', :name, '%')")
    Flux<UserEntity> findByName(@Param("name") String name);

    Flux<UserEntity> findByNameContaining(String name);
}
