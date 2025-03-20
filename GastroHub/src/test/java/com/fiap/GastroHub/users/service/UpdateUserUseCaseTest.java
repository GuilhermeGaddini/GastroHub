package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    private User mockUser;
    private CreateUpdateUserRequest updateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Configuração do usuário fictício
        mockUser = UserTestHelper.generateUser();

        // Configuração do DTO de atualização
        updateRequest = UserTestHelper.generateCreateUpdateUserRequest();

        // Configuração do DTO de resposta
        userResponse = UserTestHelper.generateUserResponse(mockUser);
    }

    @Test
    void execute_ValidIdAndRequest_UpdatesAndReturnsUserResponse() {
        // Configuração dos mocks para ID válido e mapeamento
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        doAnswer(invocation -> {
            User user = invocation.getArgument(1); // Mapeia updateRequest para mockUser
            mockUser.setName(user.getName());
            mockUser.setEmail(user.getEmail());
            return null;
        }).when(modelMapper).map(updateRequest, mockUser);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(modelMapper.map(mockUser, UserResponse.class)).thenReturn(userResponse);

        // Execução do método
        UserResponse result = updateUserUseCase.execute(mockUser.getId(), updateRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        assertEquals(updateRequest.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(modelMapper, times(1)).map(updateRequest, mockUser);
        verify(userRepository, times(1)).save(mockUser);
        verify(modelMapper, times(1)).map(mockUser, UserResponse.class);
    }

    @Test
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;

        // Configuração do mock para ID inválido
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> updateUserUseCase.execute(invalidId, updateRequest));

        // Verificações
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, times(1)).findById(invalidId);
        verify(modelMapper, never()).map(any(CreateUpdateUserRequest.class), any(User.class));
        verify(userRepository, never()).save(any(User.class));
        verify(modelMapper, never()).map(any(User.class), eq(UserResponse.class));
    }
}
