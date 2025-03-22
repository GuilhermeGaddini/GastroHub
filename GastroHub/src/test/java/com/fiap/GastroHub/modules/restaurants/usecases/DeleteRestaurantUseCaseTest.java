package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Restaurant Use Case Test Class")
public class DeleteRestaurantUseCaseTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        deleteRestaurantUseCase = new DeleteRestaurantUseCase(restaurantRepository);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should delete a restaurant successfully")
    void execute_ValidId_DeleteRestaurant() {
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
                .build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        deleteRestaurantUseCase.execute(1L);

        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).delete(restaurant);
    }

    @Test
    @DisplayName("Should throw a invalid id exception")
    void execute_InvalidId_ThrowsRestaurantException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        RestaurantException exception = assertThrows(RestaurantException.class, () -> deleteRestaurantUseCase.execute(1L));

        assertEquals("Restaurant with ID " + 1L + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw a nonexistent id exception")
    void execute_NullId_ThrowsRestaurantException() {
        RestaurantException exception = assertThrows(RestaurantException.class, () -> deleteRestaurantUseCase.execute(null));

        assertEquals("Restaurant with ID null not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, never()).delete(any());
    }
}
