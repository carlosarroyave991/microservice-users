package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.exception.DuplicateResourceException;
import com.arka.microservice.usuarios.domain.exception.ValidationException;
import com.arka.microservice.usuarios.domain.models.AddressModel;
import com.arka.microservice.usuarios.domain.models.ShippingAddressModel;
import com.arka.microservice.usuarios.domain.ports.in.IAddressPortUseCase;
import com.arka.microservice.usuarios.domain.ports.out.AddressPersistencePort;
import com.arka.microservice.usuarios.domain.ports.out.ShippingAddressPersistencePort;
import com.arka.microservice.usuarios.domain.service.address.ZipCodeValidationService;
import com.arka.microservice.usuarios.infraestructure.driven.r2dbc.entity.ShippingAddressEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

import static com.arka.microservice.usuarios.domain.exception.error.CommonErrorCode.*;

/**
 * Clase usada para implementar la logica de negocio sobre cada funcion
 */
@Service
@RequiredArgsConstructor
public class AddressUseCaseImpl implements IAddressPortUseCase {
    private final AddressPersistencePort service;
    private final ShippingAddressPersistencePort serviceShipping;
    private final ZipCodeValidationService zipCodeValidationService;

    /**
     * Servicio que obtiene todas las direcciones asociadas a un usuario.
     * Primero se recuperan las asociaciones de direcciones (shipping addresses)
     * y luego se consultan las direcciones en la tabla "address".
     *
     * @param userId identificador del usuario.
     * @return Flux que emite los modelos de dirección o un error si no se encuentran.
     */
    public Flux<AddressModel> getAddressesByUserId(Long userId) {
        return serviceShipping.findByUserId(userId)
                .flatMap(shippingAddress ->
                        service.findById(shippingAddress.getAddressId())
                                .switchIfEmpty(Mono.error(new DuplicateResourceException(DB_EMPTY)))
                )
                .switchIfEmpty(
                        Flux.error(new DuplicateResourceException(DB_EMPTY))
                );
    }

    /**
     * Servicio que obtiene todas las address existentes de forma reactiva.
     * @return retorna un Flux que emite cada address o error si la lista está vacía.
     */
    @Override
    public Flux<AddressModel> getAllAddress() {
        return service.findAll()
                .switchIfEmpty(Flux.error(new DuplicateResourceException(DB_EMPTY)));
    }

    /**
     * Servicio usado para crear una direccion de forma reactiva.
     * @param address objeto address con los parámetros necesarios para la creación.
     * @return retorna un Mono con la direccion creado o un error.
     */
    @Override
    public Mono<AddressModel> createAddressForUser(AddressModel address, Long userId) {
        //validar el codigo postal
        if (!zipCodeValidationService.isValidZipCode(address.getZipCode())){
            return Mono.error(new ValidationException(INVALID_ZIPCODE));
        }
        return service.save(address)
                .flatMap(savedAddress -> {
                    ShippingAddressModel shipping = new ShippingAddressModel();
                    shipping.setUserId(userId);
                    shipping.setAddressId(savedAddress.getId());
                    return serviceShipping.save(shipping)
                            .thenReturn(savedAddress);
                });
    }

    /**
     * Servicio que permite actualizar una direccion especifica de manera reactiva
     * @param model objeto con la data a actualizar
     * @param id identificador del objeto
     * @return retorna un Mono con la direccion actualizada o error si no existe
     */
    @Override
    public Mono<AddressModel> updateAddress(AddressModel model, Long id) {
        return service.findById(id)
                .switchIfEmpty(Mono.error(new DuplicateResourceException(ID_NOT_FOUND)))
                .flatMap(existing -> {
                    existing.setId(id);
                    if (model.getAddress() != null)existing.setAddress(model.getAddress());
                    if (model.getCity() != null)existing.setCity(model.getCity());
                    if (model.getCountry() != null)existing.setCountry(model.getCountry());
                    if (model.getStreet() != null)existing.setStreet(model.getStreet());
                    if (model.getZipCode() != null)existing.setZipCode(model.getZipCode());

                    return service.save(existing);
                });
    }

    /**
     * Servicio para eiminar un objeto de forma reactiva
     * @return retrna un Mono<Void> o emite un Mono error
     */
    @Override
    public Mono<Void> deleteAddress(Long addressId) {
        // Primero, busca el registro de ShippingAddress que relacione el usuario y la dirección a borrar.
        return service.findById(addressId)
                .switchIfEmpty(Mono.error(new DuplicateResourceException(ID_NOT_FOUND)))
                .then(service.deleteById(addressId));
    }
}
