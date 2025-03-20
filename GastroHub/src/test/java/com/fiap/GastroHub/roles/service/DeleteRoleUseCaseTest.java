package com.fiap.GastroHub.roles.service;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.DeleteRoleUseCase;
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
public class DeleteRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DeleteRoleUseCase deleteRoleUseCase;

    @Test
    void execute_ValidId_DeletesRole() {
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        deleteRoleUseCase.execute(roleId);

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        when(roleRepository.findById(invalidId)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> deleteRoleUseCase.execute(invalidId));

        assertEquals("Role with ID " + invalidId + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(invalidId);
        verify(roleRepository, never()).delete(any());
    }
}
