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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
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
        roleEntity.setId(3L);
        roleEntity.setName("test_role1");

        Role result = createRoleUseCase.execute(roleRequest);

        assertNotNull(result);
        assertEquals(roleEntity.getId(), result.getId());
        assertEquals(roleEntity.getName(), result.getName());
    }


    @Test
    @DisplayName("Error - Null Role Request")
    void execute_NullRequest_ThrowsRoleException() {
        RoleException exception = assertThrows(RoleException.class, () -> createRoleUseCase.execute(null));

        assertEquals("An unexpected error occurred while creating the role.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Role Name Already Exists")
    void execute_DuplicateRoleName_ThrowsRoleException() {
        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
        roleRequest.setName("Admin");

        RoleException exception = assertThrows(RoleException.class, () -> createRoleUseCase.execute(roleRequest));

        assertEquals("An unexpected error occurred while creating the role.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

}
