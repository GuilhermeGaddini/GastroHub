package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("Integration Test for CreateProductUseCase")
public class CreateProductsUseCaseIT {
    @Autowired
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should create a product successfully")
    void shouldCreateProductSuccessfully() {
        // Arrange
        CreateUpdateProductRequest request = new CreateUpdateProductRequest(
                "Produto 1",
                "Descrição do produto",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg",
                1L
        );

        Product product = new Product();
        product.setName("Produto 1");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setAvailability("Disponível");
        product.setPicPath("/img/path.jpeg");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        ProductResponse expectedResponse = new ProductResponse(
                "Produto 1",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg"
        );

        when(modelMapper.map(request, Product.class)).thenReturn(product);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = createProductUseCase.execute(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Produto 1", actualResponse.getName());
        assertEquals(BigDecimal.valueOf(100.00), actualResponse.getPrice());
        assertEquals("Disponível", actualResponse.getAvailability());
        assertEquals("/img/path.jpeg", actualResponse.getPicPath());

        verify(modelMapper, times(1)).map(request, Product.class);
        verify(restaurantRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
        verify(modelMapper, times(1)).map(product, ProductResponse.class);
    }
}
