package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
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
@DisplayName("Get User By ID Use Case Integration Tests")
public class GetUserByIdUseCaseIT {
    @Autowired
    private GetUserByIdUseCase getUserByIdUseCase;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("admin");
        mockUser.setAddress(null);
        mockUser.setEmail("admin@gastrohub.com");
        mockUser.setPassword("swordfish");
    }

    @Test
    @DisplayName("Success")
    void execute_ValidId_ReturnsUserResponse() {
        UserResponse result = getUserByIdUseCase.execute(mockUser.getId());

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getName(), result.getName());
        assertEquals(mockUser.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;
        UserException exception = assertThrows(UserException.class, () -> getUserByIdUseCase.execute(invalidId));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_NullId_ThrowsUserException() {
        UserException exception = assertThrows(UserException.class, () -> getUserByIdUseCase.execute(null));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
