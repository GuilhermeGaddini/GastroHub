package com.fiap.GastroHub.modules.users.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.ChangeUserPasswordRequest;
import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.LoginUserRequest;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Integration Tests for ProductController using RestAssured")
public class UserControllerIT {
    @Autowired
    private JwtUtil jwtUtil;

    @LocalServerPort
    private int port;
    private String token;
    User mockUser = new User();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        mockUser.setId(1L);
        mockUser.setName("admin");
        mockUser.setEmail("admin@gastrohub.com");
        mockUser.setPassword("swordfish");

        token = jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());
    }

    @Nested
    @DisplayName("Create cases")
    class CreateUser{
        @Test
        @DisplayName("Create user - Success")
        void createUser_success() throws Exception {
            CreateUpdateUserRequest userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            User user = UserTestHelper.generateUser();
            UserResponse userResponse = UserTestHelper.generateUserResponse(user);

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(userRequest))
                    .when()
                    .post("/users/create")
                    .then()
                    .extract().response();

            // Assert
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
            Assertions.assertEquals(userRequest.getName(), response.getBody().jsonPath().getString("name"));
            Assertions.assertEquals(userRequest.getEmail(), response.getBody().jsonPath().getString("email"));
        }

        @Test
        @DisplayName("Create user - Error - Blank email")
        void createUser_exception_blankEmail() throws Exception {
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setEmail("");

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(userRequest))
                    .when()
                    .post("/users/create")
                    .then()
                    .extract().response();

            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        }

        @Test
        @DisplayName("Create user - Error - Null email")
        void createUser_exception_nullEmail() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setEmail(null);

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(userRequest))
                    .when()
                    .post("/users/create")
                    .then()
                    .extract().response();

            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        }

        @Test
        @DisplayName("Create user - Error - Invalid email")
        void createUser_exception_invalidEmail() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setEmail("invalidemail");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(userRequest)
                    .when()
                    .put("/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Invalid email format"));
        }

        @Test
        @DisplayName("Create user - Error - Short Password")
        void createUser_exception_shortPssword() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setPassword("123");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(userRequest)
                    .when()
                    .put("/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Password must be at least 8 characters long"));

        }

        @Test
        @DisplayName("Create user - Error - Null Password")
        void createUser_exception_nullPassword() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setPassword(null);

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(userRequest)
                    .when()
                    .put("/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Password can not be empty"));
        }
    }

    @Nested
    @DisplayName("Update cases")
    class UpdateUser{
        @Test
        @DisplayName("Update user - Success")
        void updateUser_success() throws Exception {
            CreateUpdateUserRequest updatedUser = UserTestHelper.generateCreateUpdateUserRequest();
            User user = UserTestHelper.generateUser();
            UserResponse userResponse = UserTestHelper.generateUserResponse(user);

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(updatedUser)
                    .when()
                    .put("/users/{id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("John Doe"));
        }

        @Test
        @DisplayName("Update user - Error - Blank Name")
        void updateUser_exception_blankName() throws Exception {
            CreateUpdateUserRequest userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setName("");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(userRequest)
                    .when()
                    .put("/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Name can not be empty"));
        }

        @Test
        @DisplayName("Update user - Error - Null Name")
        void updateUser_exception_nullName() throws Exception {
            CreateUpdateUserRequest userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setName(null);

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(userRequest)
                    .when()
                    .put("/users/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Name can not be empty"));
        }
    }

    @Nested
    @DisplayName("Delete cases")
    class DeleteUser{
        @Test
        @DisplayName("Delete user - Success")
        void deleteUser_success() throws Exception {
            Long userId = 4L;
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/users/{id}", userId)
                    .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("Delete user - Error - ID don't Exist")
        void deleteUser_exception_idDontExist() throws Exception {
            Long userId = 999L;
            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/roles/{id}", userId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                    .path("message").equals("User with ID 999 not found"));
        }
    }

    @Nested
    @DisplayName("Get all Cases")
    class GetAllUser{
        @Test
        @DisplayName("Get all Users - Success")
        void getAllUsers_success() throws Exception {
            given()
                    .header("Authorization", "Bearer " + token)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/users")
                    .then()
                    .statusCode(200)
                    .body("$.size()", greaterThan(0));
        }
    }

    @Nested
    @DisplayName("Get User By ID - Success")
    class GetUserById{
        @Test
        void getUserById_success() throws Exception {
            Long userId = 1L;

            // Act + Assert
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/users/{id}", userId)
                    .then()
                    .statusCode(200)
                    .body("name", notNullValue())
                    .body("email", notNullValue())
                    .body("email", equalTo("admin@gastrohub.com"));
        }

        @Test
        @DisplayName("Get User By ID - Error-  ID don't exist")
        void getUserById_exception_idDontExist() throws Exception {
            Long userId = 999L;

            // Act + Assert
            String error = "User not found";
            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/users/{id}", userId)
                    .then()
                    .statusCode(400)
                    .extract()
                    .path("message").equals(error));
        }
    }

    @Nested
    @DisplayName("Login Cases")
    class LoginUser{
        @Test
        @DisplayName("Login - Success")
        void login_success() throws Exception {
            LoginUserRequest loginUserRequest = new LoginUserRequest();
            loginUserRequest.setEmail("admin@gastrohub.com");
            loginUserRequest.setPassword("swordfish");

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .extract().response();

            String tokenTest = jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());

            // Assert
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
            Assertions.assertEquals(tokenTest, response.getBody().jsonPath().getString("token"));
        }

        @Test
        @DisplayName("Login - Error - Blank Email")
        void login_exception_blankEmail() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setEmail("");
            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Email can not be empty"));
        }

        @Test
        @DisplayName("Login - Error - Null Email")
        void login_exception_nullEmail() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setEmail(null);

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Email can not be empty"));
        }

        @Test
        @DisplayName("Login - Error - Blank Password")
        void login_exception_blankPassword() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setPassword("");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Password can not be empty"));
        }

        @Test
        @DisplayName("Login - Error - Null Password")
        void login_exception_nullPassword() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setPassword(null);

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Password can not be empty"));
        }

        @Test
        @DisplayName("Login - Error - Wrong Password")
        void login_exception_wrongPassword() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setPassword("bla");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract()
                    .path("message").equals("Password can not be empty"));
        }

        @Test
        @DisplayName("Login - Error - User not found")
        void login_exception_userNotFound() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setEmail("notfound@notfound.org");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(loginUserRequest))
                    .when()
                    .post("/users/login")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract()
                    .path("message").equals("Usuário não encontrado"));
        }
    }

    @Nested
    @DisplayName("Change Password Cases")
    class ChangeUserPassword {
        @Test
        @DisplayName("Change Password - Success")
        void changePassword_success() throws Exception {
            ChangeUserPasswordRequest changeUserPasswordRequest = new ChangeUserPasswordRequest();
            changeUserPasswordRequest.setCurrentPassword("swordfish");
            changeUserPasswordRequest.setNewPassword("newpassword");
            Long userId = 1L;

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(changeUserPasswordRequest))
                    .when()
                    .put("/users/{id}/password", 1L)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

        }

        @Test
        @DisplayName("Change Password - error - Wrong Current Password")
        void changePassword_failure_wrongCurrentPassword() throws Exception {
            Long userId = 1L;
            ChangeUserPasswordRequest changeUserPasswordRequest = new ChangeUserPasswordRequest();
            changeUserPasswordRequest.setCurrentPassword("wrongPassword");
            changeUserPasswordRequest.setNewPassword("newpassword");

            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(changeUserPasswordRequest)
                .when()
                .post("/users/{id}/password", userId)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("Change Password - error - User not found")
        void changePassword_failure_userNotFound() throws Exception {
            Long userId = 999L;
            ChangeUserPasswordRequest passwordRequest = UserTestHelper.generateChangeUserPasswordRequest();

            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(passwordRequest))
                .when()
                .post("/users/{id}/password", userId)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

}
