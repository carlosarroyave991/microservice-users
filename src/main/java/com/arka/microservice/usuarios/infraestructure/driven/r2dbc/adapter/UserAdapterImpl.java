package com.arka.microservice.usuarios.infraestructure.driven.r2dbc.adapter;


import com.arka.microservice.usuarios.domain.exception.DuplicateResourceException;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.ports.out.UserPersistencePort;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.entity.UserEntity;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.mapper.IUserEntityMapper;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.arka.microservice.usuarios.domain.exception.error.CommonErrorCode.USER_NOT_FOUND;


/**
 * El adaptador se encargara de conectar ambas capas, pasando la informacion de la entidad
 * al modelo de dominio.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserAdapterImpl implements UserPersistencePort {
    private final IUserRepository repository;
    private final IUserEntityMapper mapper;

    /**
     * Funcion que consulta un objeto por id y lo mapea a modelo
     * @param id identificador el objeto a buscar
     * @return retorna un objeto mapeado para dominio
     */
    @Override
    public Mono<UserModel> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);//convierto mi entidad en modelo
    }

    /**
     * Funcion que consulta objetos por name y los mapea a modelo
     * @param name nombre de los objetos a buscar
     * @return retorna una lista de objetos mapeados para el dominio
     */
    @Override
    public Flux<UserModel> findByName(String name) {
        Flux<UserEntity> entities = (repository.findByNameContaining(name));
        return mapper.toModels(entities);
    }

    /**
     * Funcion que consulta un objeto por dni y lo mapea a modelo.
     * @param dni identificador el objeto a buscar
     * @return retorna un objeto mapeado para dominio
     */
    @Override
    public Mono<UserModel> findByDni(String dni) {
        return repository.findByDni(dni)
                .map(mapper::toModel);
    }

    /**
     * Funcion que consulta un objeto por email y lo mapea a modelo
     * @param email identificador del objeto a buscar
     * @return retorna un objeto mapeado para dominio
     */
    @Override
    public Mono<UserModel> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toModel);
    }

    /**
     * Funcion que consulta todos los objetos y los mapea a modelo.
     * @return retorna todos los objetos mapeados para el dominio
     */
    @Override
    public Flux<UserModel> findAll() {
        return mapper.toModels(repository.findAll());
    }

    /**
     * Funcion que guarda un objeto
     * @param user objeto a guardar
     * @return retorna un objeto mapeado para el dominio
     */
    /*@Override
    public Mono<UserModel> save(UserModel user) {
        UserEntity entity = mapper.toEntity(user);
        return repository.save(entity)
                .map(mapper::toModel);
    }*/

    /**
     * Funcion actualiza un objeto existente. Se busca por id
     * @param user objeto a actualizar, contiene un id en sus atributos
     * @return retorna un objeto mapeado para el dominio
     */
    @Override
    public Mono<UserModel> update(UserModel user) {
        UserEntity entity = mapper.toEntity(user);
        return repository.save(entity)
                .map(mapper::toModel);
    }

    /**
     * Funcion que eliminar un objeto dado su id y retorna el Mono<Void> correspondiente.
     * @param id identificador del objeto a eliminar
     */
    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
