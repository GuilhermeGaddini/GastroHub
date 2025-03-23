package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
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
@DisplayName("Integration Test for CreateRestaurantUseCase")
public class CreateRestaurantUseCaseIT {

    @Autowired
    private CreateRestaurantUseCase createRestaurantUseCase;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Should create a restaurant successfully")
    void shouldCreateRestaurantSuccessfully() {
        // Arrange
        User owner = new User();
        owner.setId(1L);
        owner.setName("Proprietário");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Restaurante Name");
        restaurant.setAddress("Endereço do restaurante");
        restaurant.setOwner(owner);

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        UserResponse userResponse = new UserResponse(
                1L,
                "Proprietário",
                "Proprietário@email.com",
                "Address",
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        );

        RestaurantResponse restaurantResponse = new RestaurantResponse(
                1L,
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                userResponse
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(modelMapper.map(createUpdateRestaurantRequest, Restaurant.class)).thenReturn(restaurant);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(modelMapper.map(restaurant, RestaurantResponse.class)).thenReturn(restaurantResponse);

        // Act
        RestaurantResponse actualResponse = createRestaurantUseCase.execute(createUpdateRestaurantRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Restaurant Name", actualResponse.getName());
        assertEquals("Address", actualResponse.getAddress());
        assertEquals("Proprietário", actualResponse.getOwner().getName());

        verify(userRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(createUpdateRestaurantRequest, Restaurant.class);
        verify(restaurantRepository, times(1)).save(restaurant);
        verify(modelMapper, times(1)).map(restaurant, RestaurantResponse.class);
    }

    @Test
    @DisplayName("Should throw exception when owner not found")
    void shouldThrowExceptionWhenOwnerNotFound() {
        // Arrange
        User owner = new User();
        owner.setId(1L);
        owner.setName("Proprietário");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Restaurante 1");
        restaurant.setAddress("Endereço do restaurante");
        restaurant.setOwner(owner);

        CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                "Restaurant Name",
                "Address",
                "Cuisine Type",
                "09h00 - 18h00",
                owner.getId()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            createRestaurantUseCase.execute(createUpdateRestaurantRequest);
        });

        assertEquals("An unexpected error occurred while creating the restaurant.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(userRepository, times(1)).findById(1L);
        verify(restaurantRepository, never()).save(any());
    }
}