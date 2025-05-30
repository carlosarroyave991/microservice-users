package com.arka.microservice.usuarios.infraestructure.driven.r2dbc.adapter;

import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.ports.out.AuthPersistencePort;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.entity.UserEntity;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.mapper.IUserEntityMapper;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * El adaptador se encargara de conectar ambas capas, pasando la informacion de la entidad
 * al modelo de dominio.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class AuthAdapterImpl implements AuthPersistencePort {
    private final IUserRepository repository;
    private final IUserEntityMapper mapper;


    /**
     * @param email
     * @return
     */
    @Override
    public Mono<UserModel> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toModel);
    }

    /**
     * @param model
     * @return
     */
    @Override
    public Mono<UserModel> register(UserModel model) {
        UserEntity entity = mapper.toEntity(model);
        return repository.save(entity)
                .map(mapper::toModel);
    }
}
