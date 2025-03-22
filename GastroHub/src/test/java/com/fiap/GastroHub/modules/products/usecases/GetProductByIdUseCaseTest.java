package com.fiap.GastroHub.modules.products.usecases;

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
@DisplayName("Get Product By ID Use Case Test Class")
public class GetProductByIdUseCaseTest {
    @Mock
    ProductRepository productRepository;

    private ModelMapper modelMapper;

    private GetProductByIdUseCase getProductByIdUseCase;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        getProductByIdUseCase = new GetProductByIdUseCase(productRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should get a product by it's given id")
    void execute_ValidId_ReturnsProduct() {
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
                .id(2L)
                .name("Produto 2")
                .availability("Disponível")
                .price(BigDecimal.valueOf(100.00))
                .description("Descrição do produto")
                .picPath("/img/imgPath.png")
                .restaurant(restaurant)
                .build();

        ProductResponse createProductResponse = new ProductResponse(
                "Produto 1",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg"
        );

        doReturn(createProductResponse)
                .when(modelMapper)
                .map(any(Product.class), eq(ProductResponse.class));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = getProductByIdUseCase.execute(1L);

        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should not get a product by it's given id")
    void execute_InvalidId_ThrowsProductException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductException exception = assertThrows(ProductException.class, () -> getProductByIdUseCase.execute(1L));

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, times(1)).findById(1L);
    }
}
