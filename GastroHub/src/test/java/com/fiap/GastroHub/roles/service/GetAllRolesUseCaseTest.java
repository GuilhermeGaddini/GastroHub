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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetAllRolesUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GetAllRolesUseCase getAllRolesUseCase;

    private List<Role> roles;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("Admin");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("User");

        roles = Arrays.asList(role1, role2);
    }

    @Test
    void execute_RolesExist_ReturnsListOfRoles() {
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = getAllRolesUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Admin", result.get(0).getName());
        assertEquals("User", result.get(1).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void execute_RepositoryThrowsException_ThrowsRoleException() {
        when(roleRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RoleException exception = assertThrows(RoleException.class, () -> getAllRolesUseCase.execute());

        assertEquals("Error fetching roles", exception.getMessage());
        verify(roleRepository, times(1)).findAll();
    }
}
