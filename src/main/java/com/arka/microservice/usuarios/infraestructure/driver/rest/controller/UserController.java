package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.models.UserWithAddressesModel;
import com.arka.microservice.usuarios.domain.ports.in.IUserPortUseCase;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.UserRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.UserResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.UserWithAddressResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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


    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico por su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDto> getUserById(
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable("id") Long id) {
        return service.getUserById(id)
                .map(mapperDto::toResponseWithoutId);
    }


    @Operation(summary = "Buscar usuarios por nombre", description = "Obtiene una lista de usuarios que coincidan con el nombre especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios encontrados exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron usuarios con ese nombre"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/name")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<UserResponseDto>> getUsersByName(
            @Parameter(description = "Nombre del usuario a buscar", required = true, example = "Juan")
            @RequestParam String name) {
        return service.getUsersByName(name)
                .collectList()
                .map(mapperDto::toResponseDtos);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDto> updateUser(
            @Parameter(description = "ID único del usuario a actualizar", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @Valid @RequestBody UserRequestDto requestDto) {
        UserModel model = mapperDto.toModel(requestDto);
        return service.updateUser(model, id)
                .map(mapperDto::toResponseWithoutId);
                //.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUserById(
            @Parameter(description = "ID único del usuario a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        return service.deleteUser(id)
                .onErrorResume(e -> Mono.error(e)); // Propaga explícitamente la excepción
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene una lista completa de todos los usuarios registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "No hay usuarios registrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<UserResponseDto>> getAllUsers() {
        return service.getAllUsers()
                .collectList()
                .map(models -> mapperDto.toResponseDtos(models));
    }
}
