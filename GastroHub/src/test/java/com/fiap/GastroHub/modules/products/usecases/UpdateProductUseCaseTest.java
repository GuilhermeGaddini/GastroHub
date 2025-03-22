package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Product Use Case Test Class")
public class UpdateProductUseCaseTest {
    @Mock
    ProductRepository productRepository;

    private ModelMapper modelMapper;

    private UpdateProductUseCase updateProductUseCase;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        updateProductUseCase = new UpdateProductUseCase(productRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should update a product successfully")
    void execute_ValidIdAndRequest_UpdateProduct() {
        CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                "Produto 2",
                "Descrição do produto",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg",
                1L
        );

        Product product = Product.builder()
                .id(1L)
                .name("Produto 1")
                .availability("Disponível")
                .price(BigDecimal.valueOf(100.00))
                .description("Descrição do produto")
                .picPath("/img/imgPath.png")
                .build();

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

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .products(Collections.singletonList(product))
                .build();

        Product newProduct = Product.builder()
                .id(1L)
                .name("Produto 2")
                .availability("Disponível")
                .price(BigDecimal.valueOf(100.00))
                .description("Descrição do produto")
                .picPath("/img/imgPath.png")
                .restaurant(restaurant)
                .build();

        ProductResponse updateProductResponse = new ProductResponse(
                "Produto 1",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(newProduct));
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        ProductResponse result = updateProductUseCase.execute(1L,updateProductRequest);

        assertNotNull(result);
        assertEquals("Produto 2", result.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    @DisplayName("Should not update a product successfully")
    void execute_InvalidId_ThrowsProductException() {
        CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                "Produto 2",
                "Descrição do produto",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg",
                1L
        );

        Long invalidId = 999L;

        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        ProductException exception = assertThrows(ProductException.class, () -> updateProductUseCase.execute(invalidId, updateProductRequest));

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(productRepository, times(1)).findById(invalidId);
        verify(productRepository, never()).save(any());
    }
}
