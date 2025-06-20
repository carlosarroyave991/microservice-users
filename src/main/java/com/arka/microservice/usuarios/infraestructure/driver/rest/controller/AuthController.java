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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth Controller", description = "Endpoints para la gestion de autentificacion")
public class AuthController {
    /*private final IUserPortUseCase userService;*/
    private final IAuthPortUseCase authService;
    private final IUserMapperDto mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El usuario ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> register(
            @Parameter(description = "Datos del nuevo usuario", required = true)
            @RequestBody UserRequestDto requestDto) {
        UserModel model = mapper.toModel(requestDto);
        return authService.register(model)
                .map(mapper::toResponseWithoutId);
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public Mono<AuthResponseDto> login(
            @Parameter(description = "Credenciales de acceso", required = true)
            @RequestBody @Valid AuthRequestDto authRequestDto) {
        log.info("Solicitud de login recibida para email: {}", authRequestDto.getEmail());
        AuthModel model = mapper.authReqtoModel(authRequestDto);
        return authService.authenticateUser(model)
                .map(mapper::tokenToAuthResponse)
                .doOnError(ex -> log.error("Error en la autenticación", ex));
    }
}
