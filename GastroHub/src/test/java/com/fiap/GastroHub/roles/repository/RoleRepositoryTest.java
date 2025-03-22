package com.fiap.GastroHub.roles.repository;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.helper.RoleTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Role Repository Test Class")
public class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Create - Success")
    void createRole() {
        var role = RoleTestHelper.generateRole();
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        var storedRole = roleRepository.save(role);
        verify(roleRepository, times(1)).save(role);
        assertThat(storedRole.getName()).isEqualTo(role.getName());
    }

    @Test
    @DisplayName("Get all - success")
    void getAllRoles() {
        List<Role> roles = List.of();
        when(roleRepository.findAll()).thenReturn(roles);

        var storedRoles = roleRepository.findAll();
        verify(roleRepository, times(1)).findAll();
        assertThat(storedRoles).isEqualTo(roles);
    }

    @Test
    @DisplayName("Get By ID - Success")
    void getRoleById() {
        var id = Long.valueOf(1);
        var role = RoleTestHelper.generateRole();
        role.setId(id);

        when(roleRepository.findById(any(Long.class))).thenReturn(Optional.of(role));

        var storedRole = roleRepository.findById(id);
        verify(roleRepository, times(1)).findById(id);
        assertThat(storedRole).isNotNull().containsSame(role);
    }

    @Test
    @DisplayName("Get By Name - Success")
    void getRoleByName() {
        var role = RoleTestHelper.generateRole();

        when(roleRepository.findByName(any(String.class))).thenReturn(Optional.of(role));

        var storedRole = roleRepository.findByName(role.getName());
        verify(roleRepository, times(1)).findByName(role.getName());
        assertThat(storedRole).isNotNull().containsSame(role);
    }

    @Test
    @DisplayName("Delete - Success")
    void deleteRole() {
        var id = Long.valueOf(1);

        doNothing().when(roleRepository).deleteById(any(Long.class));
        roleRepository.deleteById(id);
        verify(roleRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Get By ID - Error - Non-existent ID")
    void getRoleByIdFailure() {
        var id = 99L;
        when(roleRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        var storedRole = roleRepository.findById(id);
        verify(roleRepository, times(1)).findById(id);
        assertThat(storedRole).isEmpty();
    }

    @Test
    @DisplayName("Get By Name - Error - Non-existent Name")
    void getRoleByNameFailure() {
        var name = "Non-existent Role"; // Nome inexistente
        when(roleRepository.findByName(any(String.class))).thenReturn(Optional.empty());

        var storedRole = roleRepository.findByName(name);
        verify(roleRepository, times(1)).findByName(name);
        assertThat(storedRole).isEmpty();
    }

    @Test
    @DisplayName("Create - Error - Null Role")
    void createRoleFailure() {
        when(roleRepository.save(any(Role.class))).thenThrow(new IllegalArgumentException("Role cannot be null"));

        try {
            roleRepository.save(null);
        } catch (IllegalArgumentException e) {
            verify(roleRepository, times(1)).save(null);
            assertThat(e.getMessage()).isEqualTo("Role cannot be null");
        }
    }

    @Test
    @DisplayName("Delete - Error - Non-existent ID")
    void deleteRoleFailure() {
        var id = 99L;
        doThrow(new IllegalArgumentException("Role not found")).when(roleRepository).deleteById(any(Long.class));

        try {
            roleRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            verify(roleRepository, times(1)).deleteById(id);
            assertThat(e.getMessage()).isEqualTo("Role not found");
        }
    }

}
