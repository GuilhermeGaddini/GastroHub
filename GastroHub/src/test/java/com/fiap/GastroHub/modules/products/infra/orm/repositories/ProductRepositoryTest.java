package com.fiap.GastroHub.modules.products.infra.orm.repositories;

import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Product Repository Test Class")
public class ProductRepositoryTest {
    @Mock
    private ProductRepository productRepository;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should create a product successfully")
    void createProduct() {
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

        Mockito.when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        var storedProduct = productRepository.save(newProduct);
        verify(productRepository, times(1)).save(newProduct);
        assertThat(storedProduct.getName()).isEqualTo(newProduct.getName());
    }

    @Test
    @DisplayName("Should not create a product successfully")
    void createProductFailure() {
        Mockito.when(productRepository.save(any(Product.class))).thenThrow(new IllegalArgumentException("Product cannot be null"));

        try {
            productRepository.save(null);
        } catch (IllegalArgumentException e) {
            verify(productRepository, times(1)).save(null);
            assertThat(e.getMessage()).isEqualTo("Product cannot be null");
        }
    }

    @Test
    @DisplayName("Should get all products successfully")
    void getAllProducts() {
        List<Product> products = List.of();

        Mockito.when(productRepository.findAll()).thenReturn(products);

        var storedProducts = productRepository.findAll();
        verify(productRepository, times(1)).findAll();
        assertThat(storedProducts).isEqualTo(products);
    }

    @Test
    @DisplayName("Should get a product by it's given id")
    void getProductById() {
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

        Mockito.when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        var storedProduct = productRepository.findById(1L);
        verify(productRepository, times(1)).findById(1L);
        assertThat(storedProduct).isNotNull().containsSame(product);
    }

    @Test
    @DisplayName("Should not get a product by it's given id")
    void getProductByIdFailure() {
        Mockito.when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        var storedProduct = productRepository.findById(1L);
        verify(productRepository, times(1)).findById(1L);
        assertThat(storedProduct).isEmpty();
    }

    @Test
    @DisplayName("Should get a product by it's given name")
    void getProductByName() {
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

        Mockito.when(productRepository.findByName(any(String.class))).thenReturn(Optional.of(product));

        var storedProduct = productRepository.findByName("Produto 1");
        verify(productRepository, times(1)).findByName("Produto 1");
        assertThat(storedProduct).isNotNull().containsSame(product);
    }

    @Test
    @DisplayName("Should not get a product by it's given name")
    void getProductByNameFailure() {
        Mockito.when(productRepository.findByName(any(String.class))).thenReturn(Optional.empty());

        var storedProduct = productRepository.findByName("Produto 1");
        verify(productRepository, times(1)).findByName("Produto 1");
        assertThat(storedProduct).isEmpty();
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void deleteRole() {
        doNothing().when(productRepository).deleteById(any(Long.class));
        productRepository.deleteById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should not delete a product successfully")
    void deleteRoleFailure() {
        doThrow(new IllegalArgumentException("Product not found")).when(productRepository).deleteById(any(Long.class));

        try {
            productRepository.deleteById(1L);
        } catch (IllegalArgumentException e) {
            verify(productRepository, times(1)).deleteById(1L);
            assertThat(e.getMessage()).isEqualTo("Product not found");
        }
    }

}
