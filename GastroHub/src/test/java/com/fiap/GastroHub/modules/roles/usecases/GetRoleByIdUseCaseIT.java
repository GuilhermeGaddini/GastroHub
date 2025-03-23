package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
@DisplayName("Get Role By ID Use Case Integration Tests")
public class GetRoleByIdUseCaseIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GetRoleByIdUseCase getRoleByIdUseCase;

    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new Role(1L, "Admin");
    }

    @Test
    @DisplayName("Success")
    void execute_ValidId_ReturnsRole() {
        Role result = getRoleByIdUseCase.execute(mockRole.getId());

        assertNotNull(result);
        assertEquals(mockRole.getId(), result.getId());
        assertEquals(mockRole.getName(), result.getName());
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        RoleException exception = assertThrows(RoleException.class, () -> getRoleByIdUseCase.execute(invalidId));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
