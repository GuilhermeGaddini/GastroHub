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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Products Use Case Test Class")
public class GetAllProductUseCaseTest {
    @Mock
    ProductRepository productRepository;

    private ModelMapper modelMapper;

    private GetAllProductsUseCase getAllProductsUseCase;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        getAllProductsUseCase = new GetAllProductsUseCase(productRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should get all products successfully")
    void execute_ReturnsListOfProducts() {
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

        when(productRepository.findAll()).thenReturn(Arrays.asList(product, newProduct));

        List<ProductResponse> result = getAllProductsUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Produto 1", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should not get all products successfully")
    void execute_ThrowsProductExceptionOnError() {
        when(productRepository.findAll()).thenThrow(new ProductException("Error fetching products", HttpStatus.BAD_REQUEST));

        ProductException exception = assertThrows(ProductException.class, () -> getAllProductsUseCase.execute());

        assertEquals("Error fetching products", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, times(1)).findAll();
    }
}
