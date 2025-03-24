package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("Integration Test for GetAllProductsUseCase")
public class GetAllProductsUseCaseIT {

    @Autowired
    private GetAllProductsUseCase getAllProductsUseCase;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should retrieve all products successfully")
    void shouldRetrieveAllProductsSuccessfully() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Produto 1");
        product1.setPrice(BigDecimal.valueOf(100.00));
        product1.setAvailability("Disponível");
        product1.setPicPath("/img/path1.jpeg");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Produto 2");
        product2.setPrice(BigDecimal.valueOf(200.00));
        product2.setAvailability("Indisponível");
        product2.setPicPath("/img/path2.jpeg");

        List<Product> products = List.of(product1, product2);

        ProductResponse response1 = new ProductResponse(
                "Produto 1",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path1.jpeg"
        );

        ProductResponse response2 = new ProductResponse(
                "Produto 2",
                BigDecimal.valueOf(200.00),
                "Indisponível",
                "/img/path2.jpeg"
        );

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(product1, ProductResponse.class)).thenReturn(response1);
        when(modelMapper.map(product2, ProductResponse.class)).thenReturn(response2);

        // Act
        List<ProductResponse> actualResponses = getAllProductsUseCase.execute();

        // Assert
        assertNotNull(actualResponses);
        assertEquals(2, actualResponses.size());

        ProductResponse actualResponse1 = actualResponses.get(0);
        assertEquals("Produto 1", actualResponse1.getName());
        assertEquals(BigDecimal.valueOf(100.00), actualResponse1.getPrice());
        assertEquals("Disponível", actualResponse1.getAvailability());
        assertEquals("/img/path1.jpeg", actualResponse1.getPicPath());

        ProductResponse actualResponse2 = actualResponses.get(1);
        assertEquals("Produto 2", actualResponse2.getName());
        assertEquals(BigDecimal.valueOf(200.00), actualResponse2.getPrice());
        assertEquals("Indisponível", actualResponse2.getAvailability());
        assertEquals("/img/path2.jpeg", actualResponse2.getPicPath());

        verify(productRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(product1, ProductResponse.class);
        verify(modelMapper, times(1)).map(product2, ProductResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when error occurs in fetching products")
    void shouldThrowExceptionWhenErrorOccurs() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new ProductException("Database connection error", HttpStatus.BAD_REQUEST));

        // Act
        ProductException exception = assertThrows(ProductException.class, () -> {
            getAllProductsUseCase.execute();
        });

        // Assert
        assertEquals("Database connection error", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(productRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(), eq(ProductResponse.class));
    }
}
