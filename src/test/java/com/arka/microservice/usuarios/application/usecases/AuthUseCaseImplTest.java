package com.arka.microservice.usuarios.application.usecases;

import com.arka.microservice.usuarios.domain.exception.DuplicateResourceException;
import com.arka.microservice.usuarios.domain.exception.ValidationException;
import com.arka.microservice.usuarios.domain.models.AuthModel;
import com.arka.microservice.usuarios.domain.models.TokenModel;
import com.arka.microservice.usuarios.domain.models.UserModel;
import com.arka.microservice.usuarios.domain.models.enums.UserType;
import com.arka.microservice.usuarios.domain.ports.out.AuthPersistencePort;
import com.arka.microservice.usuarios.domain.ports.out.UserPersistencePort;
import com.arka.microservice.usuarios.domain.service.user.EmailValidationService;
import com.arka.microservice.usuarios.domain.service.user.PasswordValidationService;
import com.arka.microservice.usuarios.domain.service.user.UserTypeValidationService;
import com.arka.microservice.usuarios.domain.service.user.UsernameValidationService;
import com.arka.microservice.usuarios.infraestructure.driver.rest.config.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthUseCaseImplTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private AuthPersistencePort authPersistencePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordValidationService passwordValidationService;

    @Mock
    private UsernameValidationService usernameValidationService;

    @Mock
    private UserTypeValidationService userTypeValidationService;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private AuthUseCaseImpl authUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        AuthModel authModel = new AuthModel();
        authModel.setEmail("test@example.com");
        authModel.setPassword("password123");

        UserModel userModel = new UserModel();
        userModel.setEmail("test@example.com");
        userModel.setPassword("encodedPassword");
        userModel.setUserType(UserType.client);

        when(authPersistencePort.findByEmail("test@example.com")).thenReturn(Mono.just(userModel));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        // Act & Assert
        StepVerifier.create(authUseCase.authenticateUser(authModel))
                .expectNextMatches(tokenModel -> "jwt-token".equals(tokenModel.getAccessToken()))
                .verifyComplete();
    }

    @Test
    void authenticateUser_InvalidCredentials() {
        // Arrange
        AuthModel authModel = new AuthModel();
        authModel.setEmail("test@example.com");
        authModel.setPassword("wrongPassword");

        UserModel userModel = new UserModel();
        userModel.setEmail("test@example.com");
        userModel.setPassword("encodedPassword");

        when(authPersistencePort.findByEmail("test@example.com")).thenReturn(Mono.just(userModel));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authUseCase.authenticateUser(authModel))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void authenticateUser_UserNotFound() {
        // Arrange
        AuthModel authModel = new AuthModel();
        authModel.setEmail("nonexistent@example.com");
        authModel.setPassword("password123");

        when(authPersistencePort.findByEmail("nonexistent@example.com")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(authUseCase.authenticateUser(authModel))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void register_Success() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("testuser");
        userModel.setPassword("Password123!");
        userModel.setEmail("test@example.com");
        userModel.setDni("12345678");
        userModel.setUserType(UserType.client);

        UserModel registeredUser = new UserModel();
        registeredUser.setId(1L);
        registeredUser.setUsername("testuser");
        registeredUser.setPassword("encodedPassword");
        registeredUser.setEmail("test@example.com");
        registeredUser.setDni("12345678");
        registeredUser.setUserType(UserType.client);

        when(userTypeValidationService.isValidUserType(UserType.client)).thenReturn(true);
        when(emailValidationService.isValidEmail("test@example.com")).thenReturn(true);
        when(usernameValidationService.isValidUsername("testuser")).thenReturn(true);
        when(passwordValidationService.isValidPassword("Password123!")).thenReturn(true);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(userPersistencePort.findByDni("12345678")).thenReturn(Mono.empty());
        when(authPersistencePort.register(any(UserModel.class))).thenReturn(Mono.just(registeredUser));

        // Act & Assert
        StepVerifier.create(authUseCase.register(userModel))
                .expectNext(registeredUser)
                .verifyComplete();
    }

    @Test
    void register_InvalidUserType() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("testuser");
        userModel.setPassword("Password123!");
        userModel.setEmail("test@example.com");
        userModel.setUserType(UserType.client);

        when(userTypeValidationService.isValidUserType(UserType.client)).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authUseCase.register(userModel))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_InvalidEmail() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("testuser");
        userModel.setPassword("Password123!");
        userModel.setEmail("invalid-email");
        userModel.setUserType(UserType.client);

        when(userTypeValidationService.isValidUserType(UserType.client)).thenReturn(true);
        when(emailValidationService.isValidEmail("invalid-email")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authUseCase.register(userModel))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_InvalidUsername() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("t");  // Too short
        userModel.setPassword("Password123!");
        userModel.setEmail("test@example.com");
        userModel.setUserType(UserType.client);

        when(userTypeValidationService.isValidUserType(UserType.client)).thenReturn(true);
        when(emailValidationService.isValidEmail("test@example.com")).thenReturn(true);
        when(usernameValidationService.isValidUsername("t")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authUseCase.register(userModel))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_InvalidPassword() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("testuser");
        userModel.setPassword("weak");
        userModel.setEmail("test@example.com");
        userModel.setUserType(UserType.client);

        when(userTypeValidationService.isValidUserType(UserType.client)).thenReturn(true);
        when(emailValidationService.isValidEmail("test@example.com")).thenReturn(true);
        when(usernameValidationService.isValidUsername("testuser")).thenReturn(true);
        when(passwordValidationService.isValidPassword("weak")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authUseCase.register(userModel))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void register_DuplicateDni() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("testuser");
        userModel.setPassword("Password123!");
        userModel.setEmail("test@example.com");
        userModel.setDni("12345678");
        userModel.setUserType(UserType.client);

        UserModel existingUser = new UserModel();
        existingUser.setDni("12345678");

        when(userTypeValidationService.isValidUserType(UserType.client)).thenReturn(true);
        when(emailValidationService.isValidEmail("test@example.com")).thenReturn(true);
        when(usernameValidationService.isValidUsername("testuser")).thenReturn(true);
        when(passwordValidationService.isValidPassword("Password123!")).thenReturn(true);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(userPersistencePort.findByDni("12345678")).thenReturn(Mono.just(existingUser));

        // Act & Assert
        StepVerifier.create(authUseCase.register(userModel))
                .expectError(DuplicateResourceException.class)
                .verify();
    }
}