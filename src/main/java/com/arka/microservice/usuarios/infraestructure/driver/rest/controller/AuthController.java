package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.ports.in.IAuthPortUseCase;
import com.arka.microservice.usuarios.domain.ports.in.IUserPortUseCase;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.AuthRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.UserRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.AuthResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.UserResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.mapper.IUserMapperDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IUserPortUseCase userService;
    /*private final IAuthPortUseCase authService;*/
    private final IUserMapperDto mapper;

    /**
     * Endpoint para crear un usuario.
     * @Valid valida automaticamente los datos que llegan en el request
     * @param userRequestDto Datos del usuario a crear.
     * @return Usuario creado con código 201.
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponseDto> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserModel userModel = mapper.toModel(userRequestDto);
        return userService.createUser(userModel)
                .map(authResponse ->{
                    AuthResponseDto response = new AuthResponseDto();
                    response.setAccessToken(authResponse.getAccessToken());
                    return response;
                });
    }*/

    /*@PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AuthResponseDto> authenticate(@RequestBody AuthRequestDto requestDto){
        UserModel model = mapper.authReqtoModel(requestDto);
        return authService.authenticateUser(model)
                .map(authResponse ->{
                    AuthResponseDto response = new AuthResponseDto();
                    response.setAccessToken(authResponse.getAccessToken());
                    return response;
                });
    }*/
}
