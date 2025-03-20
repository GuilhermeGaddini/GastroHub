package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.CreateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUpdateUserRequest userRequest;
    private User userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Configuração do DTO de entrada
        userRequest = UserTestHelper.generateCreateUpdateUserRequest();

        // Configuração do mock da entidade User
        userEntity = UserTestHelper.generateUser(userRequest);

        // Configuração do mock para o DTO de resposta
        userResponse = UserTestHelper.generateUserResponse(userEntity);
    }

    @Test
    void execute_ValidRequest_CreatesAndReturnsUserResponse() {
        // Configuração dos mocks
        when(modelMapper.map(userRequest, User.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserResponse.class)).thenReturn(userResponse);

        // Execução do método
        UserResponse result = createUserUseCase.execute(userRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(userResponse.getId(), result.getId());
        assertEquals(userResponse.getName(), result.getName());
        assertEquals(userResponse.getEmail(), result.getEmail());
        verify(modelMapper, times(1)).map(userRequest, User.class);
        verify(userRepository, times(1)).save(userEntity);
        verify(modelMapper, times(1)).map(userEntity, UserResponse.class);
    }

    @Test
    void execute_SaveThrowsException_ThrowsUserException() {
        // Configuração do mock para lançar uma exceção ao salvar
        when(modelMapper.map(userRequest, User.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(new RuntimeException("Database error"));

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> createUserUseCase.execute(userRequest));

        assertEquals("An unexpected error occurred while creating the user.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, times(1)).save(userEntity);
        verify(modelMapper, never()).map(any(User.class), eq(UserResponse.class));
    }
}
