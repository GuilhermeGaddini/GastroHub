package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.modules.users.dtos.ChangeUserPasswordRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.ChangeUserPasswordUseCase;
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
@DisplayName("Change User Password Use Case Test Class")
class ChangeUserPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangeUserPasswordUseCase changeUserPasswordUseCase;

    private User mockUser;
    private ChangeUserPasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setPassword("encryptedPassword");

        passwordRequest = new ChangeUserPasswordRequest();
        passwordRequest.setCurrentPassword("encryptedPassword");
        passwordRequest.setNewPassword("newEncryptedPassword");
    }

    @Test
    @DisplayName("Success - Valid ID and Password")
    void execute_ValidIdAndMatchingPassword_ChangesPassword() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        changeUserPasswordUseCase.execute(mockUser.getId(), passwordRequest);

        // Verificações
        assertEquals(passwordRequest.getNewPassword(), mockUser.getPassword());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> changeUserPasswordUseCase.execute(invalidId, passwordRequest));

        assertEquals(String.format("User not found", invalidId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, times(1)).findById(invalidId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error - Mismatched Password")
    void execute_MismatchedPassword_ThrowsUserException() {
        passwordRequest.setCurrentPassword("wrongPassword");
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        UserException exception = assertThrows(UserException.class, () -> changeUserPasswordUseCase.execute(mockUser.getId(), passwordRequest));

        assertEquals("Password does not match", exception.getMessage());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(userRepository, never()).save(any());
    }
}
