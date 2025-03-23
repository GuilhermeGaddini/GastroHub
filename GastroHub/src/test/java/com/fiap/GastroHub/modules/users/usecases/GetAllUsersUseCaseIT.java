package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Get All Users Use Case Integration Tests")
public class GetAllUsersUseCaseIT {
    @Autowired
    private GetAllUsersUseCase getAllUsersUseCase;

    private List<User> mockUsers;

    @BeforeEach
    void setUp() {
        mockUsers = new ArrayList<>();

        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("johndoe@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("janedoe@example.com");

        mockUsers.add(user1);
        mockUsers.add(user2);
    }

    @Test
    @DisplayName("Success")
    void execute_ReturnsListOfUserResponses() {
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setName("John Doe");
        userResponse1.setEmail("johndoe@example.com");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setName("Jane Doe");
        userResponse2.setEmail("janedoe@example.com");

        List<UserResponse> result = getAllUsersUseCase.execute();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("admin", result.get(0).getName());
        assertEquals("Altair", result.get(1).getName());
    }
}
