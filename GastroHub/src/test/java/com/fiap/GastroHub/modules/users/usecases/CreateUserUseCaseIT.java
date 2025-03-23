package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
@DisplayName("Create user Use Case Integration Tests")
public class CreateUserUseCaseIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateUserUseCase createUserUseCase;

    private CreateUpdateUserRequest userRequest;
    private User userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new CreateUpdateUserRequest();
        userRequest.setName("John Doe");
        userRequest.setAddress("123 Main Street");
        userRequest.setEmail("johndoe@example.com");
        userRequest.setPassword("securepassword");

        userEntity = UserTestHelper.generateUser(userRequest);
        userEntity.setId(2L);

        userResponse = UserTestHelper.generateUserResponse(userEntity);
    }

    @Test
    @DisplayName("Success")
    void execute_ValidRequest_CreatesAndReturnsUserResponse() {
        UserResponse result = createUserUseCase.execute(userRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(userResponse.getId(), result.getId());
        assertEquals(userResponse.getName(), result.getName());
        assertEquals(userResponse.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Error - Null User")
    void execute_NullRequest_ThrowsUserException() {
        UserException exception = assertThrows(UserException.class, () -> createUserUseCase.execute(null));

        assertEquals("An unexpected error occurred while creating the user.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
