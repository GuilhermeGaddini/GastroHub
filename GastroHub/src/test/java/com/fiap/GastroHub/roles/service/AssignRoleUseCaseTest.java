package com.fiap.GastroHub.roles.service;
import com.fiap.GastroHub.modules.roles.dtos.AssignRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.AssignRoleUseCase;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
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
@DisplayName("Assign Role to User Use Case Test Class")
class AssignRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssignRoleUseCase assignRoleUseCase;

    private AssignRoleRequest request;

    @BeforeEach
    void setUp() {
        request = new AssignRoleRequest();
        request.setRoleId(1L);
        request.setUserId(2L);
    }

    @Test
    @DisplayName("Success")
    void execute_ValidRequest_AssignsRoleToUser() {
        Role role = new Role();
        role.setId(1L);
        role.setName("Admin");

        User user = new User();
        user.setId(2L);
        user.setRole(null);

        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = assignRoleUseCase.execute(request);

        assertNotNull(result);
        assertEquals(role.getId(), result.getRole().getId());
        assertEquals("Admin", result.getRole().getName());
        verify(roleRepository, times(1)).findById(request.getRoleId());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Error - Role not found")
    void execute_RoleNotFound_ThrowsRoleException() {
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> assignRoleUseCase.execute(request));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(request.getRoleId());
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error - User not found")
    void execute_UserNotFound_ThrowsRoleException() {
        Role role = new Role();
        role.setId(1L);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));

        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> assignRoleUseCase.execute(request));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(request.getRoleId());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(userRepository, never()).save(any());
    }
}