package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
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
@DisplayName("Delete Role Use Case Test Class")
public class DeleteRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DeleteRoleUseCase deleteRoleUseCase;

    @Test
    @DisplayName("Success")
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
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        when(roleRepository.findById(invalidId)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> deleteRoleUseCase.execute(invalidId));

        assertEquals("Role with ID " + invalidId + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(invalidId);
        verify(roleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Error - Null ID")
    void execute_NullId_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> deleteRoleUseCase.execute(null));

        assertEquals("Role with ID null not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(roleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Error - Role Exists but Exception During Deletion")
    void execute_DeleteThrowsException_ThrowsRoleException() {
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        doThrow(new RuntimeException("Database error during deletion")).when(roleRepository).delete(role);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deleteRoleUseCase.execute(roleId));

        assertEquals("Database error during deletion", exception.getMessage());
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).delete(role);
    }
}
