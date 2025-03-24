package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Update Role Use Case Integration Tests")
public class UpdateRoleUseCaseIT {

    @Autowired
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
        Role result = updateRoleUseCase.execute(existingRole.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
    }

    @Test
    @DisplayName("Success - No Changes")
    void execute_ValidIdNoChanges_ReturnsExistingRole() {
        updateRequest.setName("Admin");

        Role result = updateRoleUseCase.execute(existingRole.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(existingRole.getName(), result.getName());
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(invalidId, updateRequest));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Null ID")
    void execute_NullId_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(null, updateRequest));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Null Update Request")
    void execute_NullRequest_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(existingRole.getId(), null));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

}
