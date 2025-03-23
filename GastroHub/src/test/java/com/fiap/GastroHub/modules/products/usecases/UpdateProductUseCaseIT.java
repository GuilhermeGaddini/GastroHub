package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
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
@DisplayName("Integration Test for UpdateProductUseCase")
public class UpdateProductUseCaseIT {

    @Autowired
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should update a product successfully")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Produto Original");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setAvailability("Disponível");
        product.setPicPath("/img/original.jpeg");

        CreateUpdateProductRequest updateRequest = new CreateUpdateProductRequest(
                "Produto Atualizado",
                "Descrição atualizada",
                BigDecimal.valueOf(120.00),
                "Disponível",
                "/img/atualizado.jpeg",
                1L
        );

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Produto Atualizado");
        updatedProduct.setPrice(BigDecimal.valueOf(120.00));
        updatedProduct.setAvailability("Disponível");
        updatedProduct.setPicPath("/img/atualizado.jpeg");

        ProductResponse expectedResponse = new ProductResponse(
                "Produto Atualizado",
                BigDecimal.valueOf(120.00),
                "Disponível",
                "/img/atualizado.jpeg"
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(updatedProduct);
        when(modelMapper.map(updateRequest, Product.class)).thenReturn(updatedProduct);
        when(modelMapper.map(updatedProduct, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = updateProductUseCase.execute(productId, updateRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Produto Atualizado", actualResponse.getName());
        assertEquals(BigDecimal.valueOf(120.00), actualResponse.getPrice());
        assertEquals("Disponível", actualResponse.getAvailability());
        assertEquals("/img/atualizado.jpeg", actualResponse.getPicPath());

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product);
        verify(modelMapper, times(1)).map(updateRequest, product);
        verify(modelMapper, times(1)).map(updatedProduct, ProductResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        Long productId = 1L;

        CreateUpdateProductRequest updateRequest = new CreateUpdateProductRequest(
                "Produto Atualizado",
                "Descrição atualizada",
                BigDecimal.valueOf(120.00),
                "Disponível",
                "/img/atualizado.jpeg",
                1L
        );

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act + Assert
        ProductException exception = assertThrows(ProductException.class, () -> {
            updateProductUseCase.execute(productId, updateRequest);
        });

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }
}
