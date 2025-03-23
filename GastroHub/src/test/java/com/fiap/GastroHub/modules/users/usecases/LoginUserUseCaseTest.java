package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.LoginUserRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Login Use Case Test Class")
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    private User mockUser;
    private LoginUserRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setPassword("encryptedPassword");

        loginRequest = new LoginUserRequest();
        loginRequest.setEmail("johndoe@example.com");
        loginRequest.setPassword("encryptedPassword");
    }

    @Test
    @DisplayName("Success")
    void execute_ValidCredentials_ReturnsJwtToken() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail())).thenReturn("mockJwtToken");

        String result = loginUserUseCase.execute(loginRequest);

        assertNotNull(result);
        assertEquals("mockJwtToken", result);
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, times(1)).generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());
    }

    @Test
    @DisplayName("Error - User not found")
    void execute_UserNotFound_ThrowsUserException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> loginUserUseCase.execute(loginRequest));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Error - Invalid password")
    void execute_InvalidPassword_ThrowsUserException() {
        loginRequest.setPassword("wrongPassword");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));

        UserException exception = assertThrows(UserException.class, () -> loginUserUseCase.execute(loginRequest));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }
}
