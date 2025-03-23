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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("Integration Test for GetProductByIdUseCase")
public class GetProductByIdUseCaseIT {

    @Autowired
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should retrieve product by ID successfully")
    void shouldRetrieveProductByIdSuccessfully() {
        // Arrange
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Produto 1");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setAvailability("Disponível");
        product.setPicPath("/img/path.jpeg");

        ProductResponse expectedResponse = new ProductResponse(
                "Produto 1",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg"
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = getProductByIdUseCase.execute(productId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Produto 1", actualResponse.getName());
        assertEquals(BigDecimal.valueOf(100.00), actualResponse.getPrice());
        assertEquals("Disponível", actualResponse.getAvailability());
        assertEquals("/img/path.jpeg", actualResponse.getPicPath());

        verify(productRepository, times(1)).findById(productId);
        verify(modelMapper, times(1)).map(product, ProductResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act + Assert
        ProductException exception = assertThrows(ProductException.class, () -> {
            getProductByIdUseCase.execute(productId);
        });

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(productRepository, times(1)).findById(productId);
        verify(modelMapper, never()).map(any(), eq(ProductResponse.class));
    }
}

