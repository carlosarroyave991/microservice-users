package com.arka.microservice.usuarios.application.usecases;


import com.arka.microservice.usuarios.domain.exception.DuplicateResourceException;
import com.arka.microservice.usuarios.domain.exception.ValidationException;
import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.models.enums.UserType;
import com.arka.microservice.usuarios.domain.ports.in.IUserPortUseCase;
import com.arka.microservice.usuarios.domain.ports.out.UserPersistencePort;
import com.arka.microservice.usuarios.domain.service.user.EmailValidationService;
import com.arka.microservice.usuarios.domain.service.user.PasswordValidationService;
import com.arka.microservice.usuarios.domain.service.user.UserTypeValidationService;
import com.arka.microservice.usuarios.domain.service.user.UsernameValidationService;
import com.arka.microservice.usuarios.infraestructure.config.utils.DomainUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.arka.microservice.usuarios.domain.exception.error.CommonErrorCode.*;

/**
 * Clase usada para implementar la logica de negocio sobre cada funcion
 */
@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements IUserPortUseCase {
    private final UserPersistencePort servicePort;
    private final PasswordValidationService passwordValidationService;
    private final UsernameValidationService usernameValidationService;
    private final UserTypeValidationService userTypeValidationService;
    private final EmailValidationService emailValidationService;


    /**
     * Servicio para obtener un usuario especifico de forma reactiva.
     * @param id parámetro usado para la consulta del usuario.
     * @return retorna un Mono que emite el objeto o error si no se encuentra.
     */
    @Override
    public Mono<UserModel> getUserById(Long id) {
        return servicePort.findById(id)
                .switchIfEmpty(Mono.error(new DuplicateResourceException(USER_NOT_FOUND)));
    }

    /**
     * Servicio para buscar usuarios por nombre de forma reactiva.
     * @param name nombre de la persona a buscar.
     * @return retorna un Flux que emite los objetos encontrados o error si no se encuentran.
     */
    @Override
    public Flux<UserModel> getUsersByName(String name){
        return servicePort.findByName(name)
                .switchIfEmpty(Flux.error(new DuplicateResourceException(USER_NAME_NOT_FOUND)));
    }

    /**
     * Servicio que obtiene todos los usuarios existentes de forma reactiva.
     * @return retorna un Flux que emite cada usuario o error si la lista está vacía.
     */
    @Override
    public Flux<UserModel> getAllUsers() {
        return servicePort.findAll()
                .switchIfEmpty(Flux.error(new DuplicateResourceException(DB_EMPTY)));
    }

    /**
     * Servicio usado para crear un usuario de forma reactiva.
     * @param model objeto usuario con los parámetros necesarios para la creación.
     * @return retorna un Mono con el usuario creado o un error.
     */
    @Transactional
    @Override
    public Mono<UserModel> createUser(UserModel model){
        // Asignar tipo de usuario por defecto si no está definido
        if (model.getUserType() == null) model.setUserType(UserType.cliente);
        // Validar el tipo de usuario
        if (!userTypeValidationService.isValidUserType(model.getUserType())) {
            return Mono.error(new ValidationException(INVALID_USER_TYPE));
        }
        if (!emailValidationService.isValidEmail(model.getEmail())) {
            return Mono.error(new ValidationException(INVALID_EMAIL));
        }
        if (!usernameValidationService.isValidUsername(model.getUsername())){
            return Mono.error(new ValidationException(INVALID_USERNAME));
        }
        if (!passwordValidationService.isValidPassword(model.getPassword())){
            return Mono.error(new ValidationException(INVALID_PASSWORD));
        }
        //Validar si el dni ya existe en la base de datos
        return servicePort.findByDni(model.getDni())
                .flatMap(existing -> Mono.<UserModel>error(new DuplicateResourceException(DNI_ALREADY_EXISTS)))
                .switchIfEmpty(Mono.defer(() -> servicePort.save(model)));
    }


    /**
     * Servicio que permite actualizar un usuario específico de forma reactiva.
     * @param user objeto con los datos a actualizar.
     * @param id   identificador del usuario.
     * @return retorna un Mono con el usuario actualizado o error si no existe.
     */
    @Transactional
    @Override
    public Mono<UserModel> updateUser(UserModel user, Long id) {
        return servicePort.findById(id)
                .switchIfEmpty(Mono.error(new DuplicateResourceException(USER_NOT_FOUND)))
                .flatMap(existing -> {
                    existing.setId(id);
                    if (user.getName() != null)existing.setName(user.getName());
                    if (user.getEmail() != null)existing.setEmail(user.getEmail());
                    if (user.getUserType() != null)existing.setUserType(user.getUserType());
                    if (user.getDni() != null)existing.setDni(user.getDni());
                    if (user.getPhone() != null)existing.setPhone(user.getPhone());
                    if (user.getUsername() != null)existing.setUsername(user.getUsername());
                    if (user.getPassword() != null)existing.setPassword(user.getPassword());

                    return servicePort.update(existing);
                });
    }

    /**
     * Servicio para eliminar un usuario de forma reactiva.
     * @param id identificador del usuario a eliminar.
     * @return retorna un Mono<Void> que completa o emite un error si el usuario no existe.
     */
    @Override
    public Mono<Void> deleteUser(Long id) {
        return servicePort.findById(id)
                .switchIfEmpty(Mono.error(new DuplicateResourceException(DB_EMPTY)))
                .flatMap(existing -> servicePort.deleteById(id));
    }
}
