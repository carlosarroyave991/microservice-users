package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.models.enums.UserType;
import com.arka.microservice.usuarios.domain.ports.in.IAuthPortUseCase;
import com.arka.microservice.usuarios.domain.ports.in.IUserPortUseCase;
import com.arka.microservice.usuarios.infraestructure.driver.rest.config.security.JwtUtil;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.AuthRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.UserRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.AuthResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.UserResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.mapper.IUserMapperDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    /*private final IUserPortUseCase userService;*/
    private final IAuthPortUseCase authService;
    private final IUserMapperDto mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Endpoint para crear un usuario.
     * @Valid valida automaticamente los datos que llegan en el request
     * @param requestDto Datos del usuario a crear.
     * @return Usuario creado con código 201.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> register(@RequestBody UserRequestDto requestDto) {
        UserModel model = mapper.toModel(requestDto);
        return authService.register(model)
                .map(mapper::toResponseWithoutId);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto authRequestDto) {
        log.info("Solicitud de login recibida para email: {}", authRequestDto.getEmail());
        AuthModel model = mapper.authReqtoModel(authRequestDto);
        return authService.authenticateUser(model)
                .map(mapper::tokenToAuthResponse)
                .doOnError(ex -> log.error("Error en la autenticación", ex));
    }
}
