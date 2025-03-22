package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Product Use Case Test Class")
public class CreateProductUseCaseTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    private ModelMapper modelMapper;

    AutoCloseable mock;

    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        createProductUseCase = new CreateProductUseCase(productRepository, restaurantRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should execute the create product use case successfully")
    void execute_ValidRequest_CreatesAndReturnsProduct() {
        CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                "Produto 1",
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

        doReturn(newProduct)
                .when(modelMapper)
                .map(any(CreateUpdateProductRequest.class), eq(Product.class));

        doReturn(createProductResponse)
                .when(modelMapper)
                .map(any(Product.class), eq(ProductResponse.class));

        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        ProductResponse result = createProductUseCase.execute(createProductRequest);

        assertNotNull(result);
        assertEquals(createProductResponse.getName(), result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw a database error")
    void execute_RepositoryThrowsException_ThrowsProductException() {
        CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                "Produto 1",
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
                .id(2L)
                .name("Produto 2")
                .availability("Disponível")
                .price(BigDecimal.valueOf(100.00))
                .description("Descrição do produto")
                .picPath("/img/imgPath.png")
                .restaurant(restaurant)
                .build();

        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        when(modelMapper.map(createProductRequest, Product.class)).thenReturn(newProduct);
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        ProductException exception = assertThrows(ProductException.class, () -> createProductUseCase.execute(createProductRequest));

        assertEquals("An unexpected error occurred while creating the product.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw a null pointer exception")
    void execute_NullRequest_ThrowsProductException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> createProductUseCase.execute(null));

        assertEquals("Cannot invoke \"com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest.getName()\" because \"request\" is null", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw a product exception")
    void execute_DuplicateProductName_ThrowsProductException() {
        CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                "Produto 1",
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
                .id(2L)
                .name("Produto 2")
                .availability("Disponível")
                .price(BigDecimal.valueOf(100.00))
                .description("Descrição do produto")
                .picPath("/img/imgPath.png")
                .restaurant(restaurant)
                .build();

        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        when(modelMapper.map(createProductRequest, Product.class)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenThrow(new RuntimeException("Product already exists"));

        ProductException exception = assertThrows(ProductException.class, () -> createProductUseCase.execute(createProductRequest));

        assertEquals("An unexpected error occurred while creating the product.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    @DisplayName("Should throw a modelMapper exception")
    void execute_ModelMapperThrowsException_ThrowsProductException() {
        CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                "Produto 1",
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

        when(modelMapper.map(createProductRequest, Product.class)).thenThrow(new RuntimeException("Mapping error"));

        ProductException exception = assertThrows(ProductException.class, () -> createProductUseCase.execute(createProductRequest));

        assertEquals("An unexpected error occurred while creating the product.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).save(any(Product.class));
    }
}
