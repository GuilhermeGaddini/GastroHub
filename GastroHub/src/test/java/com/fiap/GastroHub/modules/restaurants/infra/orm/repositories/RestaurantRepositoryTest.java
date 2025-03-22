package com.fiap.GastroHub.modules.restaurants.infra.orm.repositories;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Restaurant Repository Test Class")
public class RestaurantRepositoryTest {
    @Mock
    private RestaurantRepository restaurantRepository;

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
    @DisplayName("Should create a restaurant successfully")
    void shouldCreateRestaurantSuccessfully() {
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

        Mockito.when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        var storedRestaurant = restaurantRepository.save(restaurant);
        verify(restaurantRepository, times(1)).save(restaurant);
        assertThat(storedRestaurant.getName()).isEqualTo(restaurant.getName());
    }

    @Test
    @DisplayName("Should not create a restaurant successfully")
    void createRestaurantFailure() {
        Mockito.when(restaurantRepository.save(any(Restaurant.class))).thenThrow(new IllegalArgumentException("Restaurant cannot be null"));

        try {
            restaurantRepository.save(null);
        } catch (IllegalArgumentException e) {
            verify(restaurantRepository, times(1)).save(null);
            assertThat(e.getMessage()).isEqualTo("Restaurant cannot be null");
        }
    }

    @Test
    @DisplayName("Should get all restaurants successfully")
    void getAllRestaurants() {
        List<Restaurant> restaurants = List.of();

        Mockito.when(restaurantRepository.findAll()).thenReturn(restaurants);

        var storedProducts = restaurantRepository.findAll();
        verify(restaurantRepository, times(1)).findAll();
        assertThat(storedProducts).isEqualTo(restaurants);
    }

    @Test
    @DisplayName("Should get a restaurant by it's given id")
    void getRestaurantById() {
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

        Mockito.when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));

        var storedProduct = restaurantRepository.findById(1L);
        verify(restaurantRepository, times(1)).findById(1L);
        assertThat(storedProduct).isNotNull().containsSame(restaurant);
    }

    @Test
    @DisplayName("Should not get a restaurant by it's given id")
    void getRestaurantByIdFailure() {
        Mockito.when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        var storedRestaurant = restaurantRepository.findById(1L);
        verify(restaurantRepository, times(1)).findById(1L);
        assertThat(storedRestaurant).isEmpty();
    }

    @Test
    @DisplayName("Should get a restaurant by it's given name")
    void getRestaurantByName() {
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

        Mockito.when(restaurantRepository.findByName(any(String.class))).thenReturn(Optional.of(restaurant));

        var storedRestaurant = restaurantRepository.findByName("Restaurant Name");
        verify(restaurantRepository, times(1)).findByName("Restaurant Name");
        assertThat(storedRestaurant).isNotNull().containsSame(restaurant);
    }

    @Test
    @DisplayName("Should not get a restaurant by it's given name")
    void getRestaurantByNameFailure() {
        Mockito.when(restaurantRepository.findByName(any(String.class))).thenReturn(Optional.empty());

        var storedRestaurant = restaurantRepository.findByName("Restaurant Name");
        verify(restaurantRepository, times(1)).findByName("Restaurant Name");
        assertThat(storedRestaurant).isEmpty();
    }

    @Test
    @DisplayName("Should delete a restaurant successfully")
    void deleteRole() {
        doNothing().when(restaurantRepository).deleteById(any(Long.class));
        restaurantRepository.deleteById(1L);
        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should not delete a restaurant successfully")
    void deleteRestaurantFailure() {
        doThrow(new IllegalArgumentException("Restaurant not found")).when(restaurantRepository).deleteById(any(Long.class));

        try {
            restaurantRepository.deleteById(1L);
        } catch (IllegalArgumentException e) {
            verify(restaurantRepository, times(1)).deleteById(1L);
            assertThat(e.getMessage()).isEqualTo("Restaurant not found");
        }
    }
}
