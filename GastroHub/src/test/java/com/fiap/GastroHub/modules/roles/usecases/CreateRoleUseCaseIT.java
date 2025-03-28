package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Create Role Use Case Integration Tests")
public class CreateRoleUseCaseIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CreateRoleUseCase createRoleUseCase;


    @Test
    @DisplayName("Success")
    void execute_ValidRequest_CreatesAndReturnsRole() {
        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
        roleRequest.setName("test_role1");

        Role roleEntity = new Role();
        roleEntity.setId(4L);
        roleEntity.setName("test_role1");

        Role result = createRoleUseCase.execute(roleRequest);

        assertNotNull(result);
        assertEquals(roleEntity.getName(), result.getName());
    }


    @Test
    @DisplayName("Error - Null Role Request")
    void execute_NullRequest_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> createRoleUseCase.execute(null));

        assertEquals("An unexpected error occurred while creating the role.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

}
