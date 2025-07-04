package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.exception.DuplicateResourceException;
import com.arka.microservice.usuarios.domain.exception.ValidationException;
import com.arka.microservice.usuarios.domain.models.AddressModel;
import com.arka.microservice.usuarios.domain.models.ShippingAddressModel;
import com.arka.microservice.usuarios.domain.ports.out.AddressPersistencePort;
import com.arka.microservice.usuarios.domain.ports.out.ShippingAddressPersistencePort;
import com.arka.microservice.usuarios.domain.service.address.ZipCodeValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class AddressUseCaseImplTest {

    @Mock
    private AddressPersistencePort addressPersistencePort;

    @Mock
    private ShippingAddressPersistencePort shippingAddressPersistencePort;

    @Mock
    private ZipCodeValidationService zipCodeValidationService;

    @InjectMocks
    private AddressUseCaseImpl addressUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAddressesByUserId_Success() {
        // Arrange
        Long userId = 1L;
        ShippingAddressModel shippingAddress = ShippingAddressModel.builder()
                .userId(userId)
                .addressId(1L)
                .build();

        AddressModel address = AddressModel.builder()
                .id(1L)
                .address("Calle Principal")
                .city("Ciudad")
                .country("País")
                .zipCode("12345")
                .build();

        when(shippingAddressPersistencePort.findByUserId(userId)).thenReturn(Flux.just(shippingAddress));
        when(addressPersistencePort.findById(1L)).thenReturn(Mono.just(address));

        // Act & Assert
        StepVerifier.create(addressUseCase.getAddressesByUserId(userId))
                .expectNext(address)
                .verifyComplete();
    }

    @Test
    void getAddressesByUserId_EmptyResult() {
        // Arrange
        Long userId = 1L;
        when(shippingAddressPersistencePort.findByUserId(userId)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(addressUseCase.getAddressesByUserId(userId))
                .expectErrorMatches(throwable -> throwable instanceof DuplicateResourceException && 
                                   ((DuplicateResourceException) throwable).getCode().equals("ERR_DB_EMPTY"))
                .verify();
    }

    @Test
    void getAllAddress_Success() {
        // Arrange
        AddressModel address1 = AddressModel.builder().id(1L).build();
        AddressModel address2 = AddressModel.builder().id(2L).build();

        when(addressPersistencePort.findAll()).thenReturn(Flux.just(address1, address2));

        // Act & Assert
        StepVerifier.create(addressUseCase.getAllAddress())
                .expectNext(address1, address2)
                .verifyComplete();
    }

    @Test
    void getAllAddress_EmptyResult() {
        // Arrange
        when(addressPersistencePort.findAll()).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(addressUseCase.getAllAddress())
                .expectErrorMatches(throwable -> throwable instanceof DuplicateResourceException && 
                                   ((DuplicateResourceException) throwable).getCode().equals("ERR_DB_EMPTY"))
                .verify();
    }

    @Test
    void createAddressForUser_Success() {
        // Arrange
        Long userId = 1L;
        AddressModel address = AddressModel.builder()
                .zipCode("12345")
                .address("Calle Principal")
                .city("Ciudad")
                .build();

        AddressModel savedAddress = AddressModel.builder()
                .id(1L)
                .zipCode("12345")
                .address("Calle Principal")
                .city("Ciudad")
                .build();

        ShippingAddressModel shippingAddress = ShippingAddressModel.builder()
                .id(1L)
                .userId(userId)
                .addressId(1L)
                .build();

        when(zipCodeValidationService.isValidZipCode("12345")).thenReturn(true);
        when(addressPersistencePort.save(any(AddressModel.class))).thenReturn(Mono.just(savedAddress));
        when(shippingAddressPersistencePort.save(any(ShippingAddressModel.class))).thenReturn(Mono.just(shippingAddress));

        // Act & Assert
        StepVerifier.create(addressUseCase.createAddressForUser(address, userId))
                .expectNext(savedAddress)
                .verifyComplete();
    }

    @Test
    void createAddressForUser_InvalidZipCode() {
        // Arrange
        Long userId = 1L;
        AddressModel address = AddressModel.builder()
                .zipCode("invalid")
                .build();

        when(zipCodeValidationService.isValidZipCode("invalid")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(addressUseCase.createAddressForUser(address, userId))
                .expectErrorMatches(throwable -> throwable instanceof ValidationException && 
                                   ((ValidationException) throwable).getCode().equals("ERR_INVALID_ZIPCODE"))
                .verify();
    }

    @Test
    void updateAddress_Success() {
        // Arrange
        Long addressId = 1L;
        AddressModel existingAddress = AddressModel.builder()
                .id(addressId)
                .address("Dirección Antigua")
                .city("Ciudad Antigua")
                .build();

        AddressModel updatedAddress = AddressModel.builder()
                .address("Nueva Dirección")
                .build();

        AddressModel resultAddress = AddressModel.builder()
                .id(addressId)
                .address("Nueva Dirección")
                .city("Ciudad Antigua")
                .build();

        when(addressPersistencePort.findById(addressId)).thenReturn(Mono.just(existingAddress));
        when(addressPersistencePort.save(any(AddressModel.class))).thenReturn(Mono.just(resultAddress));

        // Act & Assert
        StepVerifier.create(addressUseCase.updateAddress(updatedAddress, addressId))
                .expectNext(resultAddress)
                .verifyComplete();
    }

    @Test
    void updateAddress_NotFound() {
        // Arrange
        Long addressId = 1L;
        AddressModel updatedAddress = AddressModel.builder()
                .address("Nueva Dirección")
                .build();

        when(addressPersistencePort.findById(addressId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(addressUseCase.updateAddress(updatedAddress, addressId))
                .expectErrorMatches(throwable -> throwable instanceof DuplicateResourceException && 
                                   ((DuplicateResourceException) throwable).getCode().equals("ERR_ID_NOT_FOUND"))
                .verify();
    }

    @Test
    void deleteAddress_Success() {
        // Arrange
        Long addressId = 1L;
        AddressModel existingAddress = AddressModel.builder()
                .id(addressId)
                .build();

        when(addressPersistencePort.findById(addressId)).thenReturn(Mono.just(existingAddress));
        when(addressPersistencePort.deleteById(addressId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(addressUseCase.deleteAddress(addressId))
                .verifyComplete();
    }

    @Test
    void deleteAddress_NotFound() {
        // Arrange
        Long addressId = 1L;
        when(addressPersistencePort.findById(addressId)).thenReturn(Mono.empty());
        // No necesitamos configurar el comportamiento de deleteById porque nunca se llamará

        // Act & Assert
        StepVerifier.create(addressUseCase.deleteAddress(addressId))
                .expectErrorMatches(throwable -> throwable instanceof DuplicateResourceException && 
                                   ((DuplicateResourceException) throwable).getCode().equals("ERR_ID_NOT_FOUND"))
                .verify();
    }
    
    @Test
    void createAddress_Success() {
        // Arrange
        AddressModel address = AddressModel.builder()
                .zipCode("12345")
                .address("Calle Principal")
                .city("Ciudad")
                .build();

        AddressModel savedAddress = AddressModel.builder()
                .id(1L)
                .zipCode("12345")
                .address("Calle Principal")
                .city("Ciudad")
                .build();

        when(zipCodeValidationService.isValidZipCode("12345")).thenReturn(true);
        when(addressPersistencePort.save(any(AddressModel.class))).thenReturn(Mono.just(savedAddress));
        
        // Act & Assert
        StepVerifier.create(addressUseCase.createAddress(address))
                .expectNext(savedAddress)
                .verifyComplete();
    }
    
    @Test
    void createAddress_InvalidZipCode() {
        // Arrange
        AddressModel address = AddressModel.builder()
                .zipCode("invalid")
                .build();

        when(zipCodeValidationService.isValidZipCode("invalid")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(addressUseCase.createAddress(address))
                .expectErrorMatches(throwable -> throwable instanceof ValidationException && 
                                   ((ValidationException) throwable).getCode().equals("ERR_INVALID_ZIPCODE"))
                .verify();
    }
}