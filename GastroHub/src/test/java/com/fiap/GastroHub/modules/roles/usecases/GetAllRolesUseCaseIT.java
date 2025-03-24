package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Get All Roles Use Case Integration Tests")
public class GetAllRolesUseCaseIT {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GetAllRolesUseCase getAllRolesUseCase;

    private List<Role> mockRoles;

    @Test
    @DisplayName("Success")
    void execute_ReturnsListOfRoles() {

        List<Role> result = getAllRolesUseCase.execute();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Admin", result.get(0).getName());
    }
}
