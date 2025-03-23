package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("Integration Test for UpdateRestaurantUseCase")
public class UpdateRestaurantUseCaseIT {

    @Autowired
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should update a restaurant successfully")
    void shouldUpdateRestaurantSuccessfully() {
        // Arrange
        Long restaurantId = 1L;

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

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Restaurant Original");
        restaurant.setAddress("Address Original");

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant 1",
                "Address 1",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setId(restaurantId);
        updatedRestaurant.setName("Restaurant Updated");
        updatedRestaurant.setAddress("Address Updated");

        UserResponse userResponse = new UserResponse(
                1L,
                "Jorge",
                "jorge@email.com",
                "Address",
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        );

        RestaurantResponse restaurantResponse = new RestaurantResponse(
                1L,
                "Restaurant Updated",
                "Address Updated",
                "Cuisine Type",
                "09h00 - 18h00",
                userResponse
        );

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(updatedRestaurant);
        when(modelMapper.map(createUpdateRestaurantRequest, Restaurant.class)).thenReturn(updatedRestaurant);
        when(modelMapper.map(updatedRestaurant, RestaurantResponse.class)).thenReturn(restaurantResponse);

        // Act
        RestaurantResponse actualResponse = updateRestaurantUseCase.execute(restaurantId, createUpdateRestaurantRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Restaurant Updated", actualResponse.getName());
        assertEquals("Address Updated", actualResponse.getAddress());
        assertEquals("Jorge", actualResponse.getOwner().getName());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, times(1)).save(restaurant);
        verify(modelMapper, times(1)).map(createUpdateRestaurantRequest, restaurant);
        verify(modelMapper, times(1)).map(updatedRestaurant, RestaurantResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when restaurant not found")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;

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

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Restaurant Original");
        restaurant.setAddress("Address Original");

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant 1",
                "Address 1",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act + Assert
        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            updateRestaurantUseCase.execute(restaurantId, createUpdateRestaurantRequest);
        });

        assertEquals("Restaurant not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }
}
