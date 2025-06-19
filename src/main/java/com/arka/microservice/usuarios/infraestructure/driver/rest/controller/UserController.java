package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.ports.in.IUserPortUseCase;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.UserRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.UserResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.mapper.IUserMapperDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Endpoints para la gestion de usuarios")
public class UserController {
    private final IUserPortUseCase service; //capa de dominio, puerto out
    private final IUserMapperDto mapperDto;//mapper rest


    /**
     * Endpoint para obtener un usuario por su ID.
     * @param id ID del usuario a consultar.
     * @return Usuario encontrado o respuesta 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDto> getUserById(@PathVariable("id") Long id) {
        return service.getUserById(id)
                .map(mapperDto::toResponseWithoutId);
                //.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    /**
     * Endpoint para obtener usuarios por nombre.
     * @param name Nombre del usuario a buscar.
     * @return Lista de usuarios con el nombre especificado.
     */
    @GetMapping("/name")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<UserResponseDto>> getUsersByName(@RequestParam String name) {
        return service.getUsersByName(name)
                .collectList()
                .map(mapperDto::toResponseDtos);
    }

    /**
     * Endpoint para actualizar un usuario.
     * @param id ID del usuario a actualizar.
     * @param requestDto Datos del usuario actualizados.
     * @return Usuario actualizado.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDto> updateUser(@PathVariable("id") Long id,
                                            @Valid @RequestBody UserRequestDto requestDto) {
        UserModel model = mapperDto.toModel(requestDto);
        return service.updateUser(model, id)
                .map(mapperDto::toResponseWithoutId);
                //.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    /**
     * Endpoint para eliminar un usuario por su ID.
     * @param id ID del usuario a eliminar.
     * @return Respuesta 204 si se elimina correctamente.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUserById(@PathVariable Long id) {
        return service.deleteUser(id)
                .onErrorResume(e -> Mono.error(e)); // Propaga explícitamente la excepción
    }

    /**
     * Endpoint para obtener todos los usuarios.
     * @return Lista de todos los usuarios.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<UserResponseDto>> getAllUsers() {
        return service.getAllUsers()
                .collectList()
                .map(models -> mapperDto.toResponseDtos(models));
    }
}
