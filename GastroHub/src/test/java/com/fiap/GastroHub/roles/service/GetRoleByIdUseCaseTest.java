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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRoleByIdUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GetRoleByIdUseCase getRoleByIdUseCase;

    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new Role(1L, "Admin");
    }

    @Test
    void execute_ValidId_ReturnsRole() {
        // Configuração do mock
        when(roleRepository.findById(mockRole.getId())).thenReturn(Optional.of(mockRole));

        // Execução do método
        Role result = getRoleByIdUseCase.execute(mockRole.getId());

        // Verificações
        assertNotNull(result);
        assertEquals(mockRole.getId(), result.getId());
        assertEquals(mockRole.getName(), result.getName());
        verify(roleRepository, times(1)).findById(mockRole.getId());
    }

    @Test
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        // Configuração do mock para retornar vazio
        when(roleRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Execução do método e verificação da exceção
        RoleException exception = assertThrows(RoleException.class, () -> getRoleByIdUseCase.execute(invalidId));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(roleRepository, times(1)).findById(invalidId);
    }
}