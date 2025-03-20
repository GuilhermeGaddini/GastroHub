package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.modules.users.dtos.ChangeUserPasswordRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.ChangeUserPasswordUseCase;
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
class ChangeUserPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangeUserPasswordUseCase changeUserPasswordUseCase;

    private User mockUser;
    private ChangeUserPasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        // Configuração do usuário fictício
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setPassword("encryptedPassword");

        // Configuração do request de alteração de senha
        passwordRequest = new ChangeUserPasswordRequest();
        passwordRequest.setCurrentPassword("encryptedPassword");
        passwordRequest.setNewPassword("newEncryptedPassword");
    }

    @Test
    void execute_ValidIdAndMatchingPassword_ChangesPassword() {
        // Configuração dos mocks para ID válido e senha atual correspondente
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // Execução do método
        changeUserPasswordUseCase.execute(mockUser.getId(), passwordRequest);

        // Verificações
        assertEquals(passwordRequest.getNewPassword(), mockUser.getPassword());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;

        // Configuração do mock para ID inválido
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> changeUserPasswordUseCase.execute(invalidId, passwordRequest));

        // Verificações
        assertEquals(String.format("Failed to update user with id %d", invalidId), exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        verify(userRepository, times(1)).findById(invalidId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_MismatchedPassword_ThrowsUserException() {
        // Configuração do mock para ID válido, mas senha incorreta
        passwordRequest.setCurrentPassword("wrongPassword");
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> changeUserPasswordUseCase.execute(mockUser.getId(), passwordRequest));

        // Verificações
        assertEquals("Password does not match", exception.getMessage());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(userRepository, never()).save(any());
    }
}
