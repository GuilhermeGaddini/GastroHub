package com.fiap.GastroHub.modules.restaurants.infra.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.usecases.*;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Restaurant Controller test Class")
public class RestaurantControllerTest {
    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Mock
    private GetAllRestaurantsUseCase getAllRestaurantsUseCase;

    @Mock
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Mock
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @Mock
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @Mock
    private GetRestaurantMenuUseCase getRestaurantMenuUseCase;

    private MockMvc mockMvc;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        RestaurantController restaurantController = new RestaurantController(createRestaurantUseCase, updateRestaurantUseCase, getAllRestaurantsUseCase,
                getRestaurantByIdUseCase,getRestaurantMenuUseCase , deleteRestaurantUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Nested
    @DisplayName("Create restaurant cases")
    class CreateRestaurant {

        @Test
        @DisplayName("Should create a restaurant successfully")
        void createRestaurant_success() throws Exception {
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

            when(createRestaurantUseCase.execute(any(CreateUpdateRestaurantRequest.class))).thenReturn(restaurantResponse);

            mockMvc.perform(post("/restaurants/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andExpect(status().isOk());

            verify(createRestaurantUseCase, times(1)).execute(any(CreateUpdateRestaurantRequest.class));
        }

        @Test
        @DisplayName("Should not create a restaurant and throw name exception")
        public void testCreateRestaurant_exception_blankName() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "",
                    "Address",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    owner.getId()
            );

            mockMvc.perform(post("/restaurants/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Name is required"));
        }

        @Test
        @DisplayName("Should not create a restaurant and throw address exception")
        public void testCreateRestaurant_exception_blankAddress() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    owner.getId()
            );

            mockMvc.perform(post("/restaurants/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Address is required"));
        }

        @Test
        @DisplayName("Should not create a restaurant and throw cuisine type exception")
        public void testCreateRestaurant_exception_blankCuisineType() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "Address",
                    "",
                    "09h00 - 18h00",
                    owner.getId()
            );

            mockMvc.perform(post("/restaurants/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Cuisine type is required"));
        }

        @Test
        @DisplayName("Should not create a restaurant and throw opening hours exception")
        public void testCreateRestaurant_exception_blankOpeningHours() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "Address",
                    "Cuisine",
                    "",
                    owner.getId()
            );

            mockMvc.perform(post("/restaurants/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Opening hours is required"));
        }

        @Test
        @DisplayName("Should not create a restaurant and throw owner exception")
        public void testCreateRestaurant_exception_nullOwnerId() throws Exception {
            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "Address",
                    "Cuisine",
                    "09h00 - 18h00",
                    null
            );

            mockMvc.perform(post("/restaurants/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Owner ID is required"));
        }
    }

    @Nested
    @DisplayName("Update restaurant cases")
    class UpdateRestaurant {
        @Test
        @DisplayName("Should update a restaurant successfully")
        public void testUpdateRestaurant_success() throws Exception {
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
                    "User 1",
                    "email@email.com",
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

            when(updateRestaurantUseCase.execute(eq(1L), any(CreateUpdateRestaurantRequest.class))).thenReturn(restaurantResponse);

            mockMvc.perform(put("/restaurants/" + 1L).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should not update a restaurant and throw name exception")
        public void testUpdateRestaurant_exception_blankName() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "",
                    "Address",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    owner.getId()
            );

            mockMvc.perform(put("/restaurants/" + 1L).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Name is required"));
        }

        @Test
        @DisplayName("Should not update a restaurant and throw address exception")
        public void testUpdateRestaurant_exception_blankAddress() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    owner.getId()
            );

            mockMvc.perform(put("/restaurants/" + 1L).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Address is required"));
        }

        @Test
        @DisplayName("Should not update a restaurant and throw cuisine type exception")
        public void testUpdateRestaurant_exception_blankCuisineType() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "Address",
                    "",
                    "09h00 - 18h00",
                    owner.getId()
            );

            mockMvc.perform(put("/restaurants/" + 1L).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Cuisine type is required"));
        }

        @Test
        @DisplayName("Should not update a restaurant and throw opening hours exception")
        public void testUpdateRestaurant_exception_blankOpeningHours() throws Exception {
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

            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "Address",
                    "Cuisine Type",
                    "",
                    owner.getId()
            );

            mockMvc.perform(put("/restaurants/" + 1L).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Opening hours is required"));
        }

        @Test
        @DisplayName("Should not update a restaurant and throw owner exception")
        public void testUpdateRestaurant_exception_nullOwnerId() throws Exception {
            CreateUpdateRestaurantRequest createUpdateRestaurantRequest = new CreateUpdateRestaurantRequest(
                    "Restaurant Name",
                    "Address",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    null
            );

            mockMvc.perform(put("/restaurants/" + 1L).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createUpdateRestaurantRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Owner ID is required"));
        }
    }

    @Nested
    @DisplayName("Get restaurant cases")
    class GetRestaurant {
        @Test
        @DisplayName("Should get all restaurants successfully")
        public void getAllRestaurants() throws Exception {
            UserResponse userResponse1 = new UserResponse(
                    1L,
                    "Jorge",
                    "jorge@email.com",
                    "Address",
                    LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
            );

            UserResponse userResponse2 = new UserResponse(
                    2L,
                    "Jorge",
                    "jorge@email.com",
                    "Address",
                    LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
            );

            RestaurantResponse restaurantResponse1 = new RestaurantResponse(
                    1L,
                    "Restaurant Name",
                    "Address",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    userResponse1
            );

            RestaurantResponse restaurantResponse2 = new RestaurantResponse(
                    2L,
                    "Restaurant Name",
                    "Address",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    userResponse2
            );

            when(getAllRestaurantsUseCase.execute()).thenReturn(Arrays.asList(restaurantResponse1, restaurantResponse2));

            mockMvc.perform(get("/restaurants")
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andDo(print());

            verify(getAllRestaurantsUseCase, times(1)).execute();
        }

        @Test
        @DisplayName("Should get a restaurant by it's given id")
        public void getRestaurantById() throws Exception {
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
                    "Restaurant Name",
                    "Address",
                    "Cuisine Type",
                    "09h00 - 18h00",
                    userResponse
            );

            when(getRestaurantByIdUseCase.execute(eq(1L))).thenReturn(restaurantResponse);

            mockMvc.perform(get("/restaurants/{id}", 1)
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(restaurantResponse.getName()))
                    .andDo(print());

            verify(getRestaurantByIdUseCase, times(1)).execute(1L);
        }
    }

    @Nested
    @DisplayName("Delete restaurants cases")
    class DeleteRestaurant {
        @Test
        @DisplayName("Should delete a restaurant sucessfully")
        void deleteRestaurant() throws Exception {
            doNothing().when(deleteRestaurantUseCase).execute(1L);

            mockMvc.perform(delete("/restaurants/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            verify(deleteRestaurantUseCase, times(1)).execute(1L);
        }

        @Test
        @DisplayName("Should not be able to delete a restaurant")
        void deleteRestaurant_restaurantNotFound() throws Exception {
            doThrow(new RestaurantException("Restaurant not found", HttpStatus.NOT_FOUND))
                    .when(deleteRestaurantUseCase).execute(999L);

            mockMvc.perform(delete("/restaurants/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Restaurant not found"))
                    .andDo(print());

            verify(deleteRestaurantUseCase, times(1)).execute(999L);
        }
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
