package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.dtos.AssignRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
@DisplayName("Assign Role Use Case Integration Tests")
public class AssignRoleUseCaseIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignRoleUseCase assignRoleUseCase;

    @Test
    @DisplayName("Success")
    void execute_ValidRequest_AssignsRoleToUser() {
        Role role = new Role();
        role.setId(1L);
        role.setName("Admin");

        User user = new User();
        user.setId(1L);
        user.setRole(null);

        AssignRoleRequest assignRoleRequest = new AssignRoleRequest();
        assignRoleRequest.setRoleId(role.getId());
        assignRoleRequest.setUserId(user.getId());

        User result = assignRoleUseCase.execute(assignRoleRequest);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(role.getId(), result.getRole().getId());
        assertEquals("Admin", result.getRole().getName());
    }

    @Test
    @DisplayName("Error - Role not found")
    void execute_RoleNotFound_ThrowsRoleException() {
        AssignRoleRequest assignRoleRequest = new AssignRoleRequest();
        assignRoleRequest.setRoleId(999L);
        assignRoleRequest.setUserId(1L);

        RoleException exception = assertThrows(RoleException.class, () -> assignRoleUseCase.execute(assignRoleRequest));

        assertEquals("Role not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - User not found")
    void execute_UserNotFound_ThrowsRoleException() {
        AssignRoleRequest assignRoleRequest = new AssignRoleRequest();
        assignRoleRequest.setRoleId(1L);
        assignRoleRequest.setUserId(999L);

        RoleException exception = assertThrows(RoleException.class, () -> assignRoleUseCase.execute(assignRoleRequest));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
