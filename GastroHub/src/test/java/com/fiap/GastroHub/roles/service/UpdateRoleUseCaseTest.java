package com.fiap.GastroHub.roles.service;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.UpdateRoleUseCase;
import org.junit.jupiter.api.BeforeEach;
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
class UpdateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UpdateRoleUseCase updateRoleUseCase;

    private Role existingRole;
    private CreateUpdateRoleRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Mock da Role existente no banco
        existingRole = new Role(1L, "Admin");

        // Mock da Role com as novas informações para atualização
        updateRequest = new CreateUpdateRoleRequest("Super Admin");
    }

    @Test
    void execute_ValidIdAndRequest_UpdatesRole() {
        // Configuração do mock para retornar a Role existente
        when(roleRepository.findById(existingRole.getId())).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        // Execução do método
        Role result = updateRoleUseCase.execute(existingRole.getId(), updateRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(existingRole.getId(), result.getId());
        assertEquals(updateRequest.getName(), result.getName());
        verify(roleRepository, times(1)).findById(existingRole.getId());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        // Configuração do mock para retornar vazio (Role não encontrada)
        when(roleRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Execução do método e verificação da exceção
        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(invalidId, updateRequest));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(invalidId);
        verify(roleRepository, never()).save(any());
    }
}