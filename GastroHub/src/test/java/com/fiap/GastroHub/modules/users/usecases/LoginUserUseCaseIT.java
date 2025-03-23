package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.LoginUserRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
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
@DisplayName("Login Use Case Integration Tests")
public class LoginUserUseCaseIT {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginUserUseCase loginUserUseCase;

    private User mockUser;
    private LoginUserRequest loginRequest;
    String token;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("admin");
        mockUser.setEmail("admin@gastrohub.com");
        mockUser.setPassword("swordfish");

        loginRequest = new LoginUserRequest();
        loginRequest.setEmail("admin@gastrohub.com");
        loginRequest.setPassword("swordfish");

        token = jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());
    }

    @Test
    @DisplayName("Success")
    void execute_ValidCredentials_ReturnsJwtToken() {
        String result = loginUserUseCase.execute(loginRequest);

        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    @DisplayName("Error - User don't exist")
    void execute_UserNotFound_ThrowsUserException() {
        loginRequest.setEmail("wrong@email.com");
        UserException exception = assertThrows(UserException.class, () -> loginUserUseCase.execute(loginRequest));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    @DisplayName("Error - Wrong password")
    void execute_InvalidPassword_ThrowsUserException() {
        loginRequest.setPassword("wrongPassword");
        UserException exception = assertThrows(UserException.class, () -> loginUserUseCase.execute(loginRequest));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}
