package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.GetAllUsersUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GetAllUsersUseCase getAllUsersUseCase;

    private List<User> mockUsers;

    @BeforeEach
    void setUp() {
        mockUsers = new ArrayList<>();

        // Configurando usuários fictícios
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("johndoe@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("janedoe@example.com");

        mockUsers.add(user1);
        mockUsers.add(user2);
    }

    @Test
    void execute_ReturnsListOfUserResponses() {
        // Configuração do mock para o método findAll
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Configuração do mock para o mapeamento do ModelMapper
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setName("John Doe");
        userResponse1.setEmail("johndoe@example.com");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setName("Jane Doe");
        userResponse2.setEmail("janedoe@example.com");

        when(modelMapper.map(mockUsers.get(0), UserResponse.class)).thenReturn(userResponse1);
        when(modelMapper.map(mockUsers.get(1), UserResponse.class)).thenReturn(userResponse2);

        // Execução do método
        List<UserResponse> result = getAllUsersUseCase.execute();

        // Verificações
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        verify(userRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    void execute_ThrowsUserExceptionOnError() {
        // Configuração do mock para lançar uma RuntimeException
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Execução do método e verificação da exceção
        UserException exception = assertThrows(UserException.class, () -> getAllUsersUseCase.execute());

        // Verificações
        assertEquals("Error fetching users", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(User.class), eq(UserResponse.class));
    }
}
