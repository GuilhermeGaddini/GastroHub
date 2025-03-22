package com.fiap.GastroHub.users.service;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.GetUserByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Get User By ID Use Case Test Class")
class GetUserByIdUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GetUserByIdUseCase getUserByIdUseCase;

    private User mockUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        mockUser = UserTestHelper.generateUser();
        userResponse = UserTestHelper.generateUserResponse(mockUser);
    }

    @Test
    @DisplayName("Success")
    void execute_ValidId_ReturnsUserResponse() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(mockUser, UserResponse.class)).thenReturn(userResponse);

        UserResponse result = getUserByIdUseCase.execute(mockUser.getId());

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getName(), result.getName());
        assertEquals(mockUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(modelMapper, times(1)).map(mockUser, UserResponse.class);
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> getUserByIdUseCase.execute(invalidId));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, times(1)).findById(invalidId);
        verify(modelMapper, never()).map(any(), eq(UserResponse.class));
    }
}
