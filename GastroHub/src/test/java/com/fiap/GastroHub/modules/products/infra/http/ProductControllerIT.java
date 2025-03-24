package com.fiap.GastroHub.modules.products.infra.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("Integration Tests for ProductController using RestAssured")
public class ProductControllerIT {
    @Autowired
    private JwtUtil jwtUtil;

    @LocalServerPort
    private int port;

    private String token;

    @BeforeEach
    void setUp() {
        RestAssured.port = port; // Configura a porta do servidor para os testes
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("admin");
        mockUser.setEmail("admin@gastrohub.com");
        mockUser.setPassword("swordfish");

        token = jwtUtil.generateToken(mockUser.getId(), mockUser.getName(), mockUser.getEmail());
    }

    @Test
    @DisplayName("Should create a product successfully")
    void shouldCreateProductSuccessfully() throws JsonProcessingException {
        CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                "Produto 1",
                "Descrição do produto",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg",
                1L
        );

        // Act
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(createProductRequest))
                .when()
                .post("/products/create")
                .then()
                .extract().response();

        // Assert
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(createProductRequest.getName(), response.getBody().jsonPath().getString("name"));
        Assertions.assertEquals(createProductRequest.getAvailability(), response.getBody().jsonPath().getString("availability"));
    }

    @Test
    @DisplayName("Should retrieve all products successfully")
    void shouldRetrieveAllProductsSuccessfully() {
        // Act + Assert
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThan(0));
    }

    @Test
    @DisplayName("Should retrieve a product by ID successfully")
    void shouldRetrieveProductByIdSuccessfully() {
        // Arrange
        Long productId = 1L;

        // Act + Assert
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/products/{id}", productId)
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("price", notNullValue());
    }

    @Test
    @DisplayName("Should update a product successfully")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                "Produto 2",
                "Descrição do produto",
                BigDecimal.valueOf(150.00),
                "Disponível",
                "/img/path.jpeg",
                1L
        );

        // Act + Assert
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(updateProductRequest)
                .when()
                .put("/products/{id}", 1L)
                .then()
                .statusCode(200)
                .body("name", equalTo("Produto 2"))
                .body("price", equalTo(150.0f));
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void shouldDeleteProductSuccessfully() {
        // Arrange
        Long productId = 1L;

        // Act + Assert
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/products/{id}", productId)
                .then()
                .statusCode(204); // Verifica se o status de deleção é No Content
    }
}