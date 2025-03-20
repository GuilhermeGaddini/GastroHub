package com.fiap.GastroHub.roles.service;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.GetAllRolesUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllRolesUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GetAllRolesUseCase getAllRolesUseCase;

    private List<Role> mockRoles;

    @BeforeEach
    void setUp() {
        // Criação de uma lista mockada de Roles
        mockRoles = Arrays.asList(
                new Role(1L, "Admin"),
                new Role(2L, "User"),
                new Role(3L, "Manager")
        );
    }

    @Test
    void execute_ReturnsListOfRoles() {
        // Configuração do mock para o método findAll
        when(roleRepository.findAll()).thenReturn(mockRoles);

        // Execução do método
        List<Role> result = getAllRolesUseCase.execute();

        // Verificações
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Admin", result.get(0).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void execute_ThrowsRoleExceptionOnError() {
        // Configuração do mock para lançar um erro
        when(roleRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Execução do método e verificação da exceção
        RoleException exception = assertThrows(RoleException.class, () -> getAllRolesUseCase.execute());

        assertEquals("Error fetching roles", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(roleRepository, times(1)).findAll();
    }
}