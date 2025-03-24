package com.fiap.GastroHub.modules.roles.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.helper.RoleTestHelper;

import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Integration Tests for ProductController using RestAssured")
public class RoleControllerIT {
    @Autowired
    private JwtUtil jwtUtil;

    @LocalServerPort
    private int port;
    private String token;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("admin");
        mockUser.setEmail("admin@gastrohub.com");
        mockUser.setPassword("swordfish");

        token = jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());
    }

    @Nested
    @DisplayName("Create cases")
    class CreateRole {

        @Test
        @DisplayName("Create Role - Success")
        void createRole_success() throws Exception {
            CreateUpdateRoleRequest roleRequest = RoleTestHelper.generateCreateUpdateRoleRequest();
            Role role = RoleTestHelper.generateFullRole();

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(roleRequest))
                    .when()
                    .post("/roles/create")
                    .then()
                    .extract().response();

            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
            Assertions.assertEquals(roleRequest.getName(), response.getBody().jsonPath().getString("name"));
        }

        @Test
        @DisplayName("Create Role - Error - Blank Name")
        void createRole_exception_blankName() throws Exception {
            var roleRequest = new CreateUpdateRoleRequest();
            roleRequest.setName("");

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(roleRequest))
                    .when()
                    .post("/roles/create")
                    .then()
                    .extract().response();

            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        }

        @Test
        @DisplayName("Create Role - Error - Null Name")
        void createRole_exception_nullName() throws Exception {
            var roleRequest = new CreateUpdateRoleRequest();
            roleRequest.setName(null);

            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsString(roleRequest))
                    .when()
                    .post("/roles/create")
                    .then()
                    .extract().response();

            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Get cases")
    class GetRoles {

        @Test
        @DisplayName("Get all Cases")
        void getAllRoles_success() throws Exception {
            given()
                    .header("Authorization", "Bearer " + token)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/roles")
                    .then()
                    .statusCode(200)
                    .body("$.size()", greaterThan(0));
        }

        @Test
        @DisplayName("Get Role By ID - Success")
        void getRoleById_success() throws Exception {
            Long roleId = 1L;

            // Act + Assert
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/roles/{id}", roleId)
                    .then()
                    .statusCode(200)
                    .body("name", notNullValue());
        }

        @Test
        @DisplayName("Get Role By ID - Error - ID don't exist")
        void getRoleById_exception_idDontExist() throws Exception {
            Long roleId = 999L;

            // Act + Assert
            String error = "Role not found";
            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/roles/{id}", roleId)
                    .then()
                    .statusCode(400)
                    .extract()
                    .path("message").equals(error));
        }
    }

    @Nested
    @DisplayName("Update Cases")
    class UpdateRole {

        @Test
        @DisplayName("Update - Success")
        void updateRole_success() throws Exception {
            CreateUpdateRoleRequest updatedRole = RoleTestHelper.generateCreateUpdateRoleRequest();
            Role role = RoleTestHelper.generateFullRole();

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(updatedRole)
                    .when()
                    .put("/roles/{id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("test_role"));

        }

        @Test
        @DisplayName("Update - Error - Blank Name")
        void updateRole_exception_blankName() throws Exception {
            CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
            roleRequest.setName("");

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(roleRequest)
                    .when()
                    .put("/roles/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Name can not be empty"));

        }

        @Test
        @DisplayName("Update - Error - Null Name")
        void updateRole_exception_nullName() throws Exception {
            Role roleRequest = new Role();
            roleRequest.setName(null);

            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(roleRequest)
                    .when()
                    .put("/roles/{id}", 1L)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation error"))
                    .extract()
                    .path("message").equals("Name can not be empty"));

        }
    }

    @Nested
    @DisplayName("Delete Cases")
    class DeleteRole {

        @Test
        @DisplayName("Delete - Success")
        void deleteRole_success() throws Exception {
            Long roleId = 4L;
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/roles/{id}", roleId)
                    .then()
                    .statusCode(204); // Verifica se o status de deleção é No Content
        }

        @Test
        @DisplayName("Delete - Error - ID don't exist")
        void deleteRole_exception_idDontExist() throws Exception {
            Long roleId = 999L;
            String.valueOf(given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/roles/{id}", roleId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract()
                    .path("message").equals("Role with ID 999 not found"));
        }
    }
}
