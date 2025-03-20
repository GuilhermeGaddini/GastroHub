package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
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
class DeleteUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Configurando um usuário fictício para os testes
        mockUser = UserTestHelper.generateUser();
    }

    @Test
    void execute_ValidId_DeletesUser() {
        // Mock do comportamento para quando o ID é válido
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(mockUser);

        // Execução do método
        deleteUserUseCase.execute(mockUser.getId());

        // Verificações
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;

        // Mock do comportamento para quando o ID não é encontrado
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> deleteUserUseCase.execute(invalidId));

        // Asserções
        assertEquals("User with ID " + invalidId + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, times(1)).findById(invalidId);
        verify(userRepository, never()).delete(any());
    }
}
