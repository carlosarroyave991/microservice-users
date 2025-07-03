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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

