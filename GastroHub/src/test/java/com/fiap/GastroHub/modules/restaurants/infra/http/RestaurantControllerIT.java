package com.fiap.GastroHub.modules.restaurants.infra.http;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Integration Tests for RestaurantController using RestAssured")
public class RestaurantControllerIT {
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

    @Test
    @DisplayName("Should create a restaurant successfully")
    void shouldCreateRestaurantSuccessfully() throws JsonProcessingException {
        Role role = Role.builder()
                .id(1L)
                .name("Owner")
                .build();

        User owner = User.builder()
                .id(1L)
                .name("User 1")
                .address("Address 1")
                .email("email@email.com")
                .password("password")
                .role(role)
                .build();

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(createUpdateRestaurantRequest))
                .when()
                .post("/restaurants/create")
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("address", notNullValue());
    }

    @Test
    @DisplayName("Should retrieve all restaurants successfully")
    void shouldRetrieveAllRestaurantsSuccessfully() {
        // Act + Assert
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/restaurants")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThan(0));
    }

    @Test
    @DisplayName("Should retrieve a restaurant by ID successfully")
    void shouldRetrieveRestaurantByIdSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/restaurants/{id}", 1L)
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("address", notNullValue());
    }

    @Test
    @DisplayName("Should update a restaurant successfully")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        Role role = Role.builder()
                .id(1L)
                .name("Owner")
                .build();

        User owner = User.builder()
                .id(1L)
                .name("User 1")
                .address("Address 1")
                .email("email@email.com")
                .password("password")
                .role(role)
                .build();

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        // Act + Assert
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(createUpdateRestaurantRequest)
                .when()
                .put("/restaurants/{id}", 1L)
                .then()
                .statusCode(200)
                .body("name", equalTo("Restaurant Name"))
                .body("address", equalTo("Address"));
    }

    @Test
    @DisplayName("Should delete a restaurant successfully")
    void shouldDeleteRestaurantSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/restaurants/{id}", 1L)
                .then()
                .statusCode(204);
    }
}
