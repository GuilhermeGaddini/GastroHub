package com.fiap.GastroHub.roles.service;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.UpdateRoleUseCase;
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
@DisplayName("Update Role Use Case Test Class")
class UpdateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UpdateRoleUseCase updateRoleUseCase;

    private Role existingRole;
    private CreateUpdateRoleRequest updateRequest;

    @BeforeEach
    void setUp() {
        existingRole = new Role(1L, "Admin");

        updateRequest = new CreateUpdateRoleRequest("Super Admin");
    }

    @Test
    @DisplayName("Success")
    void execute_ValidIdAndRequest_UpdatesRole() {
        when(roleRepository.findById(existingRole.getId())).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        Role result = updateRoleUseCase.execute(existingRole.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(existingRole.getId(), result.getId());
        assertEquals(updateRequest.getName(), result.getName());
        verify(roleRepository, times(1)).findById(existingRole.getId());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    @DisplayName("Success - No Changes")
    void execute_ValidIdNoChanges_ReturnsExistingRole() {
        when(roleRepository.findById(existingRole.getId())).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        updateRequest.setName("Admin");

        Role result = updateRoleUseCase.execute(existingRole.getId(), updateRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(existingRole.getId(), result.getId());
        assertEquals(existingRole.getName(), result.getName());
        verify(roleRepository, times(1)).findById(existingRole.getId());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        when(roleRepository.findById(invalidId)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(invalidId, updateRequest));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(invalidId);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error - Null ID")
    void execute_NullId_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(null, updateRequest));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error - Null Update Request")
    void execute_NullRequest_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(existingRole.getId(), null));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error - Role Repository Throws Exception")
    void execute_SaveThrowsException_ThrowsRoleException() {
        when(roleRepository.findById(existingRole.getId())).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> updateRoleUseCase.execute(existingRole.getId(), updateRequest));

        assertEquals("Database error", exception.getMessage());
        verify(roleRepository, times(1)).findById(existingRole.getId());
        verify(roleRepository, times(1)).save(existingRole);
    }

}