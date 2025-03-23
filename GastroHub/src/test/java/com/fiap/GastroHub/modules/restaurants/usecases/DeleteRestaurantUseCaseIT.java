package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("Integration Test for DeleteRestaurantUseCase")
public class DeleteRestaurantUseCaseIT {

    @Autowired
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("Should delete a restaurant successfully")
    void shouldDeleteRestaurantSuccessfully() {
        // Arrange
        Long restaurantId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Restaurante 1");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantRepository).delete(restaurant);

        // Act
        deleteRestaurantUseCase.execute(restaurantId);

        // Assert
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, times(1)).delete(restaurant);
    }

    @Test
    @DisplayName("Should throw exception when restaurant not found")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act + Assert
        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            deleteRestaurantUseCase.execute(restaurantId);
        });

        assertEquals("Restaurant with ID " + restaurantId + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, never()).delete(any());
    }
}
