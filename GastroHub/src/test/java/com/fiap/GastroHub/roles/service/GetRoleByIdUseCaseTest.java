package com.fiap.GastroHub.roles.service;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.GetRoleByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetRoleByIdUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GetRoleByIdUseCase getRoleByIdUseCase;

    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role();
        role.setId(1L);
        role.setName("Admin");
    }

    @Test
    void execute_ValidId_ReturnsRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role result = getRoleByIdUseCase.execute(1L);

        assertNotNull(result);
        assertEquals(role.getId(), result.getId());
        assertEquals(role.getName(), result.getName());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void execute_RoleNotFound_ThrowsRoleException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> getRoleByIdUseCase.execute(1L));

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
    }
}
