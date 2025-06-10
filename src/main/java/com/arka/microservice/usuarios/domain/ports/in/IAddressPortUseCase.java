package com.arka.microservice.usuarios.domain.ports.in;

import com.arka.microservice.usuarios.domain.models.AddressModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Se definen las operaciones REST que pueden utilizar para interactuar
 * con el nucreo del sistema.
 */
public interface IAddressPortUseCase {
    // Metodo para obtener las direcciones filtradas por el userId.
    Flux<AddressModel> getAddressesByUserId(Long userId);

    // Devuelve todos las direcciones como un flujo reactivo
    Flux<AddressModel> getAllAddress();

    // Crea una direccion y retorna la direccion creada
    Mono<AddressModel> createAddressForUser(AddressModel model, Long userId);

    // Crea una direccion y retorna la direccion creada
    Mono<AddressModel> createAddress(AddressModel model);

    // Actualiza una direccion y retorna la direccion actualizada
    Mono<AddressModel> updateAddress(AddressModel model, Long id);

    // Elimina una direccion y retorna una señal de completado
    Mono<Void> deleteAddress(Long userId);
}
