package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import com.arka.microservice.usuarios.domain.models.AddressModel;
import com.arka.microservice.usuarios.domain.ports.in.IAddressPortUseCase;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.req.AddressRequestDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.AddressResponseDto;
import com.arka.microservice.usuarios.infraestructure.driver.rest.mapper.IAddressMapperDto;
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

    /**
     * Endpoint para obtener todas las direcciones pertenecientes a un usuario.
     * Ejemplo de uso: GET /api/users/123/addresses
     * @param userId identificador del usuario.
     * @return Flux que emite las direcciones del usuario transformadas a DTO.
     */
    @GetMapping("/users/{userId}/addresses")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AddressResponseDto> getAddressesByUser(@PathVariable Long userId) {
        return serviceAddress.getAddressesByUserId(userId)
                .map(mapperAddress::toResponse);
    }

    /**
     * Endpoint para crear una nueva dirección de manera generalizada.
     * La validación de datos (por ejemplo, @Valid) se realiza en el DTO recibido.
     * @param addressRequestDto Datos de la dirección.
     * @return La dirección creada en forma de DTO.
     */
    @PostMapping("/address")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AddressResponseDto> createAddress(@Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressModel addressModel = mapperAddress.toModel(addressRequestDto);
        return serviceAddress.createAddress(addressModel)
                .map(mapperAddress::toResponse);
    }

    /**
     * Endpoint para obtener todas las direcciones.
     * @return Un Flux de direcciones como DTO.
     */
    @GetMapping("/addresses/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AddressResponseDto> getAllAddresses() {
        return serviceAddress.getAllAddress()
                .map(mapperAddress::toResponse);
    }

    /**
     * Endpoint para crear una nueva dirección asociada a un usuario.
     * La validación de datos (por ejemplo, @Valid) se realiza en el DTO recibido.
     * @param userId Identificador del usuario al que se asociará la dirección.
     * @param addressRequestDto Datos de la dirección.
     * @return La dirección creada en forma de DTO.
     */
    @PostMapping("/users/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AddressResponseDto> createAddressForUser(@PathVariable Long userId,
                                                         @Valid @RequestBody AddressRequestDto addressRequestDto) {
        // Convertir el DTO a un modelo de dominio.
        AddressModel addressModel = mapperAddress.toModel(addressRequestDto);
        return serviceAddress.createAddressForUser(addressModel, userId)
                .map(mapperAddress::toResponse);
    }

    /**
     * Endpoint para actualizar una dirección existente.
     * @param id Identificador de la dirección a actualizar.
     * @param addressRequestDto Datos actualizados de la dirección.
     * @return La dirección actualizada en forma de DTO.
     */
    @PutMapping("/users/{userId}/addresses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AddressResponseDto> updateAddress(@PathVariable Long id,
                                                  @PathVariable Long userId,
                                                  @Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressModel addressModel = mapperAddress.toModel(addressRequestDto);
        return serviceAddress.updateAddress(addressModel, id)
                .map(mapperAddress::toResponse);
    }

    /**
     * Endpoint para eliminar una dirección por su ID
     * @return Un Mono vacío que indica que la operación se completó.
     */
    @DeleteMapping("/users/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAddress(@PathVariable Long addressId, @PathVariable Long userId) {
        return serviceAddress.deleteAddress(addressId);
    }
}
