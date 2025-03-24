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
@DisplayName("Integration Test for GetRestaurantByIdUseCase")
public class GetRestaurantByIdUseCaseIT {

    @Autowired
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should retrieve restaurant by ID successfully")
    void shouldRetrieveRestaurantByIdSuccessfully() {
        // Arrange
        Long restaurantId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Restaurant 1");
        restaurant.setAddress("Address 1");

        UserResponse userResponse = new UserResponse(
                1L,
                "Owner",
                "owner@email.com",
                "Address",
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        );

        RestaurantResponse restaurantResponse = new RestaurantResponse(
                1L,
                "Restaurant 1",
                "Address 1",
                "Cuisine Type",
                "09h00 - 18h00",
                userResponse
        );

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(modelMapper.map(restaurant, RestaurantResponse.class)).thenReturn(restaurantResponse);

        // Act
        RestaurantResponse actualResponse = getRestaurantByIdUseCase.execute(restaurantId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Restaurant 1", actualResponse.getName());
        assertEquals("Address 1", actualResponse.getAddress());
        assertEquals("Owner", actualResponse.getOwner().getName());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(modelMapper, times(1)).map(restaurant, RestaurantResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when restaurant not found")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act + Assert
        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            getRestaurantByIdUseCase.execute(restaurantId);
        });

        assertEquals("Restaurant not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(modelMapper, never()).map(any(), eq(RestaurantResponse.class));
    }
}
