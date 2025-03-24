package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.ChangeUserPasswordRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Change user password Use Case Integration Tests")
public class ChangeUserPasswordUseCaseIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChangeUserPasswordUseCase changeUserPasswordUseCase;

    private User mockUser;
    private ChangeUserPasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("admin");
        mockUser.setEmail("admin@admin.com");
        mockUser.setPassword("swordfish");

        passwordRequest = new ChangeUserPasswordRequest();
        passwordRequest.setCurrentPassword("swordfish");
        passwordRequest.setNewPassword("newPassword");
    }

    @Test
    @DisplayName("Success - Valid ID and Password")
    void execute_ValidIdAndMatchingPassword_ChangesPassword() {
        changeUserPasswordUseCase.execute(mockUser.getId(), passwordRequest);

        assertEquals("swordfish", mockUser.getPassword());
    }

    @Test
    @DisplayName("Error - Invalid ID")
    void execute_InvalidId_ThrowsUserException() {
        Long invalidId = 999L;

        UserException exception = assertThrows(UserException.class, () -> changeUserPasswordUseCase.execute(invalidId, passwordRequest));

        assertEquals(String.format("User not found", invalidId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Mismatched Password")
    void execute_MismatchedPassword_ThrowsUserException() {
        passwordRequest.setCurrentPassword("wrongPassword");

        UserException exception = assertThrows(UserException.class, () -> changeUserPasswordUseCase.execute(mockUser.getId(), passwordRequest));

        assertEquals("Password does not match", exception.getMessage());
    }
}
