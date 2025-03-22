package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.GetAllUsersUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Get All Users Use Case Test Class")
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
    @DisplayName("Success")
    void execute_ReturnsListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(mockUsers);

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

        List<UserResponse> result = getAllUsersUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        verify(userRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Error - Database error")
    void execute_ThrowsUserExceptionOnError() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        UserException exception = assertThrows(UserException.class, () -> getAllUsersUseCase.execute());

        assertEquals("Error fetching users", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(User.class), eq(UserResponse.class));
    }
}
