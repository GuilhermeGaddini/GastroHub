package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("Integration Test for GetAllRestaurantsUseCase")
public class GetAllRestaurantsUseCaseIT {

    @Autowired
    private GetAllRestaurantsUseCase getAllRestaurantsUseCase;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should retrieve all restaurants successfully")
    void shouldRetrieveAllRestaurantsSuccessfully() {
        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("Restaurante 1");
        restaurant1.setAddress("Endereço 1");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Restaurante 2");
        restaurant2.setAddress("Endereço 2");

        List<Restaurant> restaurants = List.of(restaurant1, restaurant2);

        UserResponse userResponse = new UserResponse(
                1L,
                "Owner",
                "owner@email.com",
                "Address",
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        );

        RestaurantResponse restaurantResponse1 = new RestaurantResponse(
                1L,
                "Restaurant 1",
                "Address 1",
                "Cuisine Type",
                "09h00 - 18h00",
                userResponse
        );

        RestaurantResponse restaurantResponse2 = new RestaurantResponse(
                1L,
                "Restaurant 2",
                "Address 2",
                "Cuisine Type",
                "09h00 - 18h00",
                userResponse
        );

        when(restaurantRepository.findAll()).thenReturn(restaurants);
        when(modelMapper.map(restaurant1, RestaurantResponse.class)).thenReturn(restaurantResponse1);
        when(modelMapper.map(restaurant2, RestaurantResponse.class)).thenReturn(restaurantResponse2);

        // Act
        List<RestaurantResponse> actualResponses = getAllRestaurantsUseCase.execute();

        // Assert
        assertNotNull(actualResponses);
        assertEquals(2, actualResponses.size());

        RestaurantResponse actualResponse1 = actualResponses.get(0);
        assertEquals("Restaurant 1", actualResponse1.getName());
        assertEquals("Address 1", actualResponse1.getAddress());
        assertEquals("Owner", actualResponse1.getOwner().getName());

        RestaurantResponse actualResponse2 = actualResponses.get(1);
        assertEquals("Restaurant 2", actualResponse2.getName());
        assertEquals("Address 2", actualResponse2.getAddress());
        assertEquals("Owner", actualResponse2.getOwner().getName());

        verify(restaurantRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(restaurant1, RestaurantResponse.class);
        verify(modelMapper, times(1)).map(restaurant2, RestaurantResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when error occurs while fetching restaurants")
    void shouldThrowExceptionWhenErrorOccurs() {
        // Arrange
        when(restaurantRepository.findAll()).thenThrow(new RestaurantException("Error fetching restaurants", HttpStatus.BAD_REQUEST));

        // Act + Assert
        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            getAllRestaurantsUseCase.execute();
        });

        assertEquals("Error fetching restaurants", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(restaurantRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(), eq(RestaurantResponse.class));
    }
}
