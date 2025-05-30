package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.exception.DuplicateResourceException;
import com.arka.microservice.usuarios.domain.exception.ValidationException;
import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.TokenModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.models.enums.UserType;
import com.arka.microservice.usuarios.domain.ports.in.IAuthPortUseCase;
import com.arka.microservice.usuarios.domain.ports.out.AuthPersistencePort;
import com.arka.microservice.usuarios.domain.ports.out.UserPersistencePort;
import com.arka.microservice.usuarios.domain.service.user.EmailValidationService;
import com.arka.microservice.usuarios.domain.service.user.PasswordValidationService;
import com.arka.microservice.usuarios.domain.service.user.UserTypeValidationService;
import com.arka.microservice.usuarios.domain.service.user.UsernameValidationService;
import com.arka.microservice.usuarios.infraestructure.config.utils.DomainUserDetails;
import com.arka.microservice.usuarios.infraestructure.driver.rest.config.security.JwtUtil;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.arka.microservice.usuarios.domain.exception.error.CommonErrorCode.*;

/**
 * Clase usada para implementar la logica de negocio sobre cada funcion
 */
@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements IAuthPortUseCase{
    private final UserPersistencePort serviceUser;
    private final AuthPersistencePort serviceAuth;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordValidationService passwordValidationService;
    private final UsernameValidationService usernameValidationService;
    private final UserTypeValidationService userTypeValidationService;
    private final EmailValidationService emailValidationService;

    @Override
    public Mono<TokenModel> authenticateUser(AuthModel model) {
        return serviceAuth.findByEmail(model.getEmail())
                // Verificamos que la contraseña enviada coincida con la encriptada almacenada.
                .filter(user -> passwordEncoder.matches(model.getPassword(), user.getPassword()))
                .map(user -> {
                    // Construimos el objeto UserDetails que requiere JwtUtil para generar el token.
                    User userDetails = (User) User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getUserType().name())
                            .build();
                    // Generamos el token JWT
                    String token = jwtUtil.generateToken(userDetails);
                    TokenModel auth = new TokenModel();
                    auth.setAccessToken(token);
                    return auth;
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Credenciales inválidas")));
    }


    /**
     * Servicio usado para crear un usuario de forma reactiva.
     * @param model objeto usuario con los parámetros necesarios para la creación.
     * @return retorna un Mono con el usuario creado o un error.
     */
    @Transactional
    @Override
    public Mono<UserModel> register(UserModel model){
        // Asignar tipo de usuario por defecto si no está definido
        if (model.getUserType() == null) model.setUserType(UserType.client);
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
        model.setPassword(passwordEncoder.encode(model.getPassword()));
        //Validar si el dni ya existe en la base de datos
        return serviceUser.findByDni(model.getDni())
                .flatMap(existing -> Mono.<UserModel>error(new DuplicateResourceException(DNI_ALREADY_EXISTS)))
                .switchIfEmpty(Mono.defer(() -> serviceAuth.register(model)));
    }
}
