package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Delete Role Use Case Integration Tests")
public class DeleteRoleUseCaseIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DeleteRoleUseCase deleteRoleUseCase;

    @Test
    @DisplayName("Success")
    void execute_ValidId_DeletesRole() {
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);

        deleteRoleUseCase.execute(roleId);
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsRoleException() {
        Long invalidId = 999L;

        RoleException exception = assertThrows(RoleException.class, () -> deleteRoleUseCase.execute(invalidId));

        assertEquals("Role with ID " + invalidId + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Null ID")
    void execute_NullId_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> deleteRoleUseCase.execute(null));

        assertEquals("Role with ID null not allowed", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
