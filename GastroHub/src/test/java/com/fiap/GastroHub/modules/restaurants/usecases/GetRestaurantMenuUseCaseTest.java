package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Restaurant Menu Use Case Test Class")
class GetRestaurantMenuUseCaseTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private GetRestaurantMenuUseCase getRestaurantMenuUseCase;

    private Restaurant restaurant;
    private List<Product> menu;

    @BeforeEach
    void setUp() {
        // Criação de um menu fictício
        menu = List.of(
                new Product(1L, "Pizza", "Delicious cheese pizza", BigDecimal.valueOf(9.99), "Local ou entrega", "C:\\GastroHub\\images\\pizza.png", null),
                new Product(2L, "Burger", "Classic beef burger", BigDecimal.valueOf(5.99), "Local ou entrega", "C:\\GastroHub\\images\\pizza.png", null)
        );

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("GastroHub Restaurant");
        restaurant.setProducts(menu);
    }

    @Test
    @DisplayName("Success - Valid Restaurant ID")
    void execute_ValidId_ReturnsMenu() {
        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        List<Product> result = getRestaurantMenuUseCase.execute(restaurant.getId());

        assertNotNull(result);
        assertEquals(menu.size(), result.size());
        assertEquals(menu.get(0).getName(), result.get(0).getName());
        verify(restaurantRepository, times(1)).findById(restaurant.getId());
    }

    @Test
    @DisplayName("Error - Restaurant Not Found")
    void execute_InvalidId_ThrowsRestaurantException() {
        Long invalidId = 999L;

        when(restaurantRepository.findById(invalidId)).thenReturn(Optional.empty());

        RestaurantException exception = assertThrows(RestaurantException.class, () -> getRestaurantMenuUseCase.execute(invalidId));

        assertEquals("Restaurant not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Success - Empty Menu")
    void execute_ValidIdWithEmptyMenu_ReturnsEmptyList() {
        restaurant.setProducts(new ArrayList<>());

        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        List<Product> result = getRestaurantMenuUseCase.execute(restaurant.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restaurantRepository, times(1)).findById(restaurant.getId());
    }
}
