package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.exception.NotFoundException;
import com.arka.microservice.usuarios.domain.exception.ValidationException;
import com.arka.microservice.usuarios.domain.models.AddressModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.models.enums.UserType;
import com.arka.microservice.usuarios.domain.ports.out.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserUseCaseImplTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private AddressUseCaseImpl addressUseCase;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_Success() {
        // Arrange
        Long userId = 1L;
        UserModel user = new UserModel();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        List<AddressModel> addresses = Arrays.asList(
                new AddressModel(1L, "Address 1", "City 1", "Country 1", "Street 1", "12345"),
                new AddressModel(2L, "Address 2", "City 2", "Country 2", "Street 2", "67890")
        );

        when(userPersistencePort.findById(userId)).thenReturn(Mono.just(user));
        when(addressUseCase.getAddressesByUserId(userId)).thenReturn(Flux.fromIterable(addresses));

        // Act & Assert
        StepVerifier.create(userUseCase.getUserById(userId))
                .expectNextMatches(result -> {
                    return result.getId().equals(userId) &&
                           result.getName().equals("Test User") &&
                           result.getAddressModelList().size() == 2;
                })
                .verifyComplete();
    }

    @Test
    void getUserById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userPersistencePort.findById(userId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.getUserById(userId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void getUserById_AddressesNotFound() {
        // Arrange
        Long userId = 1L;
        UserModel user = new UserModel();
        user.setId(userId);
        user.setName("Test User");

        when(userPersistencePort.findById(userId)).thenReturn(Mono.just(user));
        when(addressUseCase.getAddressesByUserId(userId)).thenReturn(Flux.error(new RuntimeException("Error")));

        // Act & Assert
        StepVerifier.create(userUseCase.getUserById(userId))
                .expectNextMatches(result -> {
                    return result.getId().equals(userId) &&
                           result.getName().equals("Test User") &&
                           result.getAddressModelList() != null &&
                           result.getAddressModelList().isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getUsersByName_Success() {
        // Arrange
        String name = "Test";
        UserModel user1 = new UserModel();
        user1.setId(1L);
        user1.setName("Test User 1");

        UserModel user2 = new UserModel();
        user2.setId(2L);
        user2.setName("Test User 2");

        when(userPersistencePort.findByName(name)).thenReturn(Flux.just(user1, user2));

        // Act & Assert
        StepVerifier.create(userUseCase.getUsersByName(name))
                .expectNext(user1, user2)
                .verifyComplete();
    }

    @Test
    void getUsersByName_NotFound() {
        // Arrange
        String name = "NonExistent";
        when(userPersistencePort.findByName(name)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.getUsersByName(name))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        UserModel user1 = new UserModel();
        user1.setId(1L);
        user1.setName("User 1");

        UserModel user2 = new UserModel();
        user2.setId(2L);
        user2.setName("User 2");

        when(userPersistencePort.findAll()).thenReturn(Flux.just(user1, user2));

        // Act & Assert
        StepVerifier.create(userUseCase.getAllUsers())
                .expectNext(user1, user2)
                .verifyComplete();
    }

    @Test
    void getAllUsers_EmptyResult() {
        // Arrange
        when(userPersistencePort.findAll()).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.getAllUsers())
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void updateUser_Success() {
        // Arrange
        Long userId = 1L;
        UserModel existingUser = new UserModel();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setUserType(UserType.client);

        UserModel updateData = new UserModel();
        updateData.setName("New Name");
        updateData.setEmail("new@example.com");

        UserModel updatedUser = new UserModel();
        updatedUser.setId(userId);
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@example.com");
        updatedUser.setUserType(UserType.client);

        when(userPersistencePort.findById(userId)).thenReturn(Mono.just(existingUser));
        when(userPersistencePort.update(any(UserModel.class))).thenReturn(Mono.just(updatedUser));

        // Act & Assert
        StepVerifier.create(userUseCase.updateUser(updateData, userId))
                .expectNext(updatedUser)
                .verifyComplete();
    }

    @Test
    void updateUser_NotFound() {
        // Arrange
        Long userId = 1L;
        UserModel updateData = new UserModel();
        updateData.setName("New Name");

        when(userPersistencePort.findById(userId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.updateUser(updateData, userId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        Long userId = 1L;
        UserModel existingUser = new UserModel();
        existingUser.setId(userId);

        when(userPersistencePort.findById(userId)).thenReturn(Mono.just(existingUser));
        when(userPersistencePort.deleteById(userId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.deleteUser(userId))
                .verifyComplete();
    }

    @Test
    void deleteUser_NotFound() {
        // Arrange
        Long userId = 1L;
        when(userPersistencePort.findById(userId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.deleteUser(userId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void deleteUser_NullId() {
        // Act & Assert
        StepVerifier.create(userUseCase.deleteUser(null))
                .expectError(ValidationException.class)
                .verify();
    }
}