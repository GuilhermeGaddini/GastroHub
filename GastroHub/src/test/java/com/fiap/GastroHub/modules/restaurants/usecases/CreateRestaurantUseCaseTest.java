package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Restaurant Use Case Test Class")
public class CreateRestaurantUseCaseTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    private CreateRestaurantUseCase createRestaurantUseCase;

    private ModelMapper modelMapper;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        createRestaurantUseCase = new CreateRestaurantUseCase(restaurantRepository, userRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should execute the create restaurant use case successfully")
    void execute_ValidRequest_CreatesAndReturnsRestaurant() {
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

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .build();

        doReturn(restaurant)
                .when(modelMapper)
                .map(any(CreateUpdateRestaurantRequest.class), eq(Restaurant.class));

        doReturn(restaurantResponse)
                .when(modelMapper)
                .map(any(Restaurant.class), eq(RestaurantResponse.class));

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(owner));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponse result = createRestaurantUseCase.execute(createUpdateRestaurantRequest);

        assertNotNull(result);
        assertEquals(restaurantResponse.getName(), result.getName());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should not create a restaurant and throw a database error")
    void execute_RepositoryThrowsException_ThrowsRestaurantException() {
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

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(owner));
        when(modelMapper.map(createUpdateRestaurantRequest, Restaurant.class)).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenThrow(new RestaurantException("Database error", HttpStatus.BAD_REQUEST));

        RestaurantException exception = assertThrows(RestaurantException.class, () -> createRestaurantUseCase.execute(createUpdateRestaurantRequest));

        assertEquals("An unexpected error occurred while creating the restaurant.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should not create a restaurant and throw a null pointer exception")
    void execute_NullRequest_ThrowsRestaurantException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> createRestaurantUseCase.execute(null));

        assertEquals("Cannot invoke \"com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest.getName()\" because \"request\" is null", exception.getMessage());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should not create a restaurant and throw a restaurant exception")
    void execute_DuplicateRestaurantName_ThrowsRestaurantException() {
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

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(owner));
        when(modelMapper.map(createUpdateRestaurantRequest, Restaurant.class)).thenReturn(restaurant);
        when(restaurantRepository.save(restaurant)).thenThrow(new RuntimeException("Restaurant already exists"));

        RestaurantException exception = assertThrows(RestaurantException.class, () -> createRestaurantUseCase.execute(createUpdateRestaurantRequest));

        assertEquals("An unexpected error occurred while creating the restaurant.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    @DisplayName("Should not create a restaurant and throw a modelMapper exception")
    void execute_ModelMapperThrowsException_ThrowsRestaurantException() {
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

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant Name")
                .address("Restaurant Address")
                .cuisineType("Restaurant CuisineType")
                .openingHours("Restaurant OpeningHours")
                .owner(owner)
                .build();

        when(modelMapper.map(createUpdateRestaurantRequest, Restaurant.class)).thenThrow(new RuntimeException("Mapping error"));

        RestaurantException exception = assertThrows(RestaurantException.class, () -> createRestaurantUseCase.execute(createUpdateRestaurantRequest));

        assertEquals("An unexpected error occurred while creating the restaurant.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }
}
