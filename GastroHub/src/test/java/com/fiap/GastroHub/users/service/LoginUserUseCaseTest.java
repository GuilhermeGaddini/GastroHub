package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.modules.users.dtos.LoginUserRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.LoginUserUseCase;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
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
        // Configuração do usuário fictício
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setPassword("encryptedPassword");

        // Configuração do request de login
        loginRequest = new LoginUserRequest();
        loginRequest.setEmail("johndoe@example.com");
        loginRequest.setPassword("encryptedPassword");
    }

    @Test
    void execute_ValidCredentials_ReturnsJwtToken() {
        // Configuração dos mocks para credenciais válidas
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail())).thenReturn("mockJwtToken");

        // Execução do método
        String result = loginUserUseCase.execute(loginRequest);

        // Verificações
        assertNotNull(result);
        assertEquals("mockJwtToken", result);
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, times(1)).generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());
    }

    @Test
    void execute_UserNotFound_ThrowsUserException() {
        // Configuração do mock para usuário não encontrado
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> loginUserUseCase.execute(loginRequest));

        // Verificações
        assertEquals("Usuário não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void execute_InvalidPassword_ThrowsUserException() {
        // Configuração do mock para senha inválida
        loginRequest.setPassword("wrongPassword");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> loginUserUseCase.execute(loginRequest));

        // Verificações
        assertEquals("Usuário ou senha inválidos", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }
}
