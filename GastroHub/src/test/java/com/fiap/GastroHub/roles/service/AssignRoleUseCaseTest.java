package com.fiap.GastroHub.roles.service;

import com.fiap.GastroHub.modules.roles.dtos.AssignRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.AssignRoleUseCase;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AssignRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssignRoleUseCase assignRoleUseCase;

    private AssignRoleRequest request;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new AssignRoleRequest();
        request.setRoleId(1L);
        request.setUserId(1L);

        role = new Role();
        role.setId(1L);
        role.setName("Admin");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void execute_ValidRequest_AssignsRoleToUser() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = assignRoleUseCase.execute(request);

        assertNotNull(result);
        assertEquals(role, result.getRole());
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void execute_RoleNotFound_ThrowsRoleException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> assignRoleUseCase.execute(request));

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void execute_UserNotFound_ThrowsRoleException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> assignRoleUseCase.execute(request));

        assertEquals("User not found", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
