package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import com.arka.microservice.usuarios.domain.models.AddressModel;
import com.arka.microservice.usuarios.domain.ports.in.IAddressPortUseCase;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.AddressRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.AddressResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.mapper.IAddressMapperDto;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Address Controller", description = "Endpoints para la gestion de direcciones del usuario")
public class AddressController {
    private final IAddressPortUseCase serviceAddress;
    private final IAddressMapperDto mapperAddress;

    @Operation(summary = "Obtener direcciones por usuario", description = "Obtiene todas las direcciones asociadas a un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Direcciones encontradas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado o sin direcciones"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/users/{userId}/addresses")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AddressResponseDto> getAddressesByUser(
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return serviceAddress.getAddressesByUserId(userId)
                .map(mapperAddress::toResponse);
    }

    @Operation(summary = "Crear dirección general", description = "Crea una nueva dirección sin asociar a un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dirección creada exitosamente",
                content = @Content(schema = @Schema(implementation = AddressResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/address")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AddressResponseDto> createAddress(
            @Parameter(description = "Datos de la nueva dirección", required = true)
            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressModel addressModel = mapperAddress.toModel(addressRequestDto);
        return serviceAddress.createAddress(addressModel)
                .map(mapperAddress::toResponse);
    }

    @Operation(summary = "Obtener todas las direcciones", description = "Obtiene una lista completa de todas las direcciones registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de direcciones obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "No hay direcciones registradas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/addresses/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AddressResponseDto> getAllAddresses() {
        return serviceAddress.getAllAddress()
                .map(mapperAddress::toResponse);
    }

    @Operation(summary = "Crear dirección para usuario", description = "Crea una nueva dirección y la asocia a un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dirección creada y asociada exitosamente",
                content = @Content(schema = @Schema(implementation = AddressResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/users/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AddressResponseDto> createAddressForUser(
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Datos de la nueva dirección", required = true)
            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        // Convertir el DTO a un modelo de dominio.
        AddressModel addressModel = mapperAddress.toModel(addressRequestDto);
        return serviceAddress.createAddressForUser(addressModel, userId)
                .map(mapperAddress::toResponse);
    }

    @Operation(summary = "Actualizar dirección", description = "Actualiza los datos de una dirección existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dirección actualizada exitosamente",
                content = @Content(schema = @Schema(implementation = AddressResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Dirección o usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/users/{userId}/addresses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AddressResponseDto> updateAddress(
            @Parameter(description = "ID único de la dirección", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Datos actualizados de la dirección", required = true)
            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressModel addressModel = mapperAddress.toModel(addressRequestDto);
        return serviceAddress.updateAddress(addressModel, id)
                .map(mapperAddress::toResponse);
    }

    @Operation(summary = "Eliminar dirección", description = "Elimina una dirección específica del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dirección eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Dirección o usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/users/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAddress(
            @Parameter(description = "ID único de la dirección a eliminar", required = true, example = "1")
            @PathVariable Long addressId,
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return serviceAddress.deleteAddress(addressId);
    }
}
