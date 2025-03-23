package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
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
@DisplayName("Update User Use Case Integration Tests")
public class UpdateUserUseCaseIT {
    @Autowired
    private UpdateUserUseCase updateUserUseCase;

    private User mockUser;
    private CreateUpdateUserRequest updateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        mockUser = UserTestHelper.generateUser();

        updateRequest = UserTestHelper.generateCreateUpdateUserRequest();

        userResponse = UserTestHelper.generateUserResponse(mockUser);
    }

    @Test
    @DisplayName("Success")
    void execute_ValidIdAndRequest_UpdatesAndReturnsUserResponse() {
        UserResponse result = updateUserUseCase.execute(mockUser.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        assertEquals(updateRequest.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;
        UserException exception = assertThrows(UserException.class, () -> updateUserUseCase.execute(invalidId, updateRequest));

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
