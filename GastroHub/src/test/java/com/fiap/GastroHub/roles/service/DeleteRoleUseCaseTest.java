package com.fiap.GastroHub.roles.service;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.DeleteRoleUseCase;
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
public class DeleteRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DeleteRoleUseCase deleteRoleUseCase;

    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role();
        role.setId(1L);
        role.setName("Admin");
    }

    @Test
    void execute_ValidId_DeletesRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(role);

        assertDoesNotThrow(() -> deleteRoleUseCase.execute(1L));
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void execute_RoleNotFound_ThrowsRoleException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> deleteRoleUseCase.execute(1L));

        assertEquals("Role with ID 1 not found", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, never()).delete(any(Role.class));
    }
}
