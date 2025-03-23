package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Restaurants Use Case Test Class")
public class GetAllRestaurantUseCaseTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    private GetAllRestaurantsUseCase getAllRestaurantsUseCase;

    private ModelMapper modelMapper;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        getAllRestaurantsUseCase = new GetAllRestaurantsUseCase(restaurantRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should get all restaurants successfully")
    void execute_ReturnsListOfRestaurants() {
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

        UserResponse userResponse = new UserResponse(
                1L,
                "Jorge",
                "jorge@email.com",
                "Address",
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        );

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        RestaurantResponse restaurantResponse = new RestaurantResponse(
                1L,
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                userResponse
        );

        Restaurant restaurant1 = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .id(2L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .build();

        doReturn(restaurantResponse)
                .when(modelMapper)
                .map(any(Restaurant.class), eq(RestaurantResponse.class));

        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(restaurant1, restaurant2));

        List<RestaurantResponse> result = getAllRestaurantsUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Restaurant Name", result.get(0).getName());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should not get all restaurants with exception")
    void execute_ThrowsRestaurantExceptionOnError() {
        when(restaurantRepository.findAll()).thenThrow(new ProductException("Error fetching restaurants", HttpStatus.BAD_REQUEST));

        RestaurantException exception = assertThrows(RestaurantException.class, () -> getAllRestaurantsUseCase.execute());

        assertEquals("Error fetching restaurants", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Success - Empty Restaurant List")
    void execute_ReturnsEmptyListWhenNoRestaurantsExist() {
        when(restaurantRepository.findAll()).thenReturn(List.of());

        List<RestaurantResponse> result = getAllRestaurantsUseCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Error - ModelMapper Throws Exception")
    void execute_ModelMapperThrowsException() {
        // Configurando um restaurante válido
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(User.builder()
                        .id(1L)
                        .name("Owner")
                        .email("owner@email.com")
                        .build())
                .build();

        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        doThrow(new RuntimeException("Error mapping restaurant"))
                .when(modelMapper).map(any(Restaurant.class), eq(RestaurantResponse.class));

        RestaurantException exception = assertThrows(RestaurantException.class, () -> getAllRestaurantsUseCase.execute());

        assertEquals("Error fetching restaurants", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Success - Multiple Restaurants with Different Owners")
    void execute_ReturnsListWithMultipleRestaurantsAndOwners() {
        Role ownerRole = Role.builder()
                .id(1L)
                .name("Owner")
                .build();

        User owner1 = User.builder()
                .id(1L)
                .name("Owner 1")
                .email("owner1@email.com")
                .role(ownerRole)
                .build();

        User owner2 = User.builder()
                .id(2L)
                .name("Owner 2")
                .email("owner2@email.com")
                .role(ownerRole)
                .build();

        Restaurant restaurant1 = Restaurant.builder()
                .id(1L)
                .name("Restaurant 1")
                .address("Address 1")
                .cuisineType("Cuisine 1")
                .openingHours("09h00 - 18h00")
                .owner(owner1)
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .id(2L)
                .name("Restaurant 2")
                .address("Address 2")
                .cuisineType("Cuisine 2")
                .openingHours("10h00 - 20h00")
                .owner(owner2)
                .build();

        RestaurantResponse response1 = new RestaurantResponse(1L, "Restaurant 1", "Address 1", "Cuisine 1", "09h00 - 18h00",
                new UserResponse(owner1.getId(), owner1.getName(), owner1.getEmail(), owner1.getAddress(), LocalDateTime.now(), LocalDateTime.now()));
        RestaurantResponse response2 = new RestaurantResponse(2L, "Restaurant 2", "Address 2", "Cuisine 2", "10h00 - 20h00",
                new UserResponse(owner2.getId(), owner2.getName(), owner2.getEmail(), owner2.getAddress(), LocalDateTime.now(), LocalDateTime.now()));

        doReturn(response1).when(modelMapper).map(restaurant1, RestaurantResponse.class);
        doReturn(response2).when(modelMapper).map(restaurant2, RestaurantResponse.class);

        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant1, restaurant2));

        List<RestaurantResponse> result = getAllRestaurantsUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Restaurant 1", result.get(0).getName());
        assertEquals("Restaurant 2", result.get(1).getName());
        verify(restaurantRepository, times(1)).findAll();
    }
}
