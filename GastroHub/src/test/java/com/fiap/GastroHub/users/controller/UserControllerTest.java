package com.fiap.GastroHub.users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.helper.UserTestHelper;
import com.fiap.GastroHub.modules.users.dtos.*;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.http.UserController;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.usecases.*;
import com.fiap.GastroHub.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("User Controller test Class")
public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private GetAllUsersUseCase getAllUsersUseCase;

    @Mock
    private GetUserByIdUseCase getUserByIdUseCase;

    @Mock
    private LoginUserUseCase loginUserUseCase;

    @Mock
    private ChangeUserPasswordUseCase changeUserPasswordUseCase;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        UserController userController = new UserController(createUserUseCase, updateUserUseCase, changeUserPasswordUseCase, getAllUsersUseCase, getUserByIdUseCase, deleteUserUseCase, loginUserUseCase);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
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
    @DisplayName("Create cases")
    class CreateUser{
        @Test
        @DisplayName("Create user - Success")
        void createUser_success() throws Exception {
            CreateUpdateUserRequest userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            User user = UserTestHelper.generateUser();
            UserResponse userResponse = UserTestHelper.generateUserResponse(user);

            when(createUserUseCase.execute(userRequest)).thenReturn(userResponse);

            mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isOk());

            verify(createUserUseCase, times(1)).execute(any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Create user - Error - Blank email")
        void createUser_exception_blankEmail() throws Exception {
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setEmail("");

            mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Email can not be empty"));

            verify(createUserUseCase, never()).execute(any(CreateUpdateUserRequest.class));

        }

        @Test
        @DisplayName("Create user - Error - Null email")
        void createUser_exception_nullEmail() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setEmail(null);

            mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Email can not be empty"));

            verify(createUserUseCase, never()).execute(any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Create user - Error - Invalid email")
        void createUser_exception_invalidEmail() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setEmail("invalidemail");

            mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Invalid email format"));

            verify(createUserUseCase, never()).execute(any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Create user - Error - Short Password")
        void createUser_exception_shortPssword() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setPassword("123");

            mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Password must be at least 8 characters long"));

            verify(createUserUseCase, never()).execute(any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Create user - Error - Null Password")
        void createUser_exception_nullPassword() throws Exception{
            var userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setPassword(null);

            mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Password can not be empty"));

            verify(createUserUseCase, never()).execute(any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Create user - Error - xmlPayload")
        void createUser_exception_xmlPayload() throws Exception{
            String xmlPayload = "<user><id>2</id><name>Regular User</name></user>";

            mockMvc.perform(post("/users/create")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(createUserUseCase, never()).execute(any(CreateUpdateUserRequest.class));
        }
    }

    @Nested
    @DisplayName("Update cases")
    class UpdateUser{
        @Test
        @DisplayName("Update user - Success")
        void updateUser_success() throws Exception {
            CreateUpdateUserRequest updatedUser = UserTestHelper.generateCreateUpdateUserRequest();
            User user = UserTestHelper.generateUser();
            UserResponse userResponse = UserTestHelper.generateUserResponse(user);

            when(updateUserUseCase.execute(eq(1L), any(CreateUpdateUserRequest.class))).thenReturn(userResponse);

            mockMvc.perform(put("/users/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                    .andDo(print());

            verify(updateUserUseCase, times(1)).execute(eq(1L), any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Update user - Error - Blank Name")
        void updateUser_exception_blankName() throws Exception {
            CreateUpdateUserRequest userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setName("");

            mockMvc.perform(put("/users/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"))
                    .andDo(print());

            verify(updateUserUseCase, never()).execute(eq(1L), any(CreateUpdateUserRequest.class));
        }

        @Test
        @DisplayName("Update user - Error - Null Name")
        void updateUser_exception_nullName() throws Exception {
            CreateUpdateUserRequest userRequest = UserTestHelper.generateCreateUpdateUserRequest();
            userRequest.setName(null);

            mockMvc.perform(put("/users/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"))
                    .andDo(print());

            verify(updateUserUseCase, never()).execute(eq(1L), any(CreateUpdateUserRequest.class));
        }
    }

    @Nested
    @DisplayName("Delete cases")
    class DeleteUser{
        @Test
        @DisplayName("Delete user - Success")
        void deleteUser_success() throws Exception {
            doNothing().when(deleteUserUseCase).execute(1L);

            mockMvc.perform(delete("/users/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            verify(deleteUserUseCase, times(1)).execute(1L);
        }

        @Test
        @DisplayName("Delete user - Error - ID don't Exist")
        void deleteUser_exception_idDontExist() throws Exception {
            doThrow(new UserException("User not found", HttpStatus.NOT_FOUND))
                    .when(deleteUserUseCase).execute(999L);

            mockMvc.perform(delete("/users/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(deleteUserUseCase, times(1)).execute(999L);
        }
    }

    @Nested
    @DisplayName("Get all Cases")
    class GetAllUser{
        @Test
        @DisplayName("Get all Users - Success")
        void getAllUsers_success() throws Exception {
            User user = UserTestHelper.generateUser();
            when(getAllUsersUseCase.execute()).thenReturn(List.of(UserTestHelper.generateUserResponse(user)));

            mockMvc.perform(get("/users")
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andDo(print());

            verify(getAllUsersUseCase, times(1)).execute();
        }
    }

    @Nested
    @DisplayName("Get User By ID - Success")
    class GetUserById{
        @Test
        void getUserById_success() throws Exception {
            User user = UserTestHelper.generateUser();
            UserResponse userResponse = UserTestHelper.generateUserResponse(user);

            when(getUserByIdUseCase.execute(1L)).thenReturn(userResponse);

            mockMvc.perform(get("/users/{id}", 1)
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId()))
                    .andExpect(jsonPath("$.name").value(user.getName()))
                    .andDo(print());

            verify(getUserByIdUseCase, times(1)).execute(1L);
        }

        @Test
        @DisplayName("Get User By ID - Error-  ID don't exist")
        void getUserById_exception_idDontExist() throws Exception {
            when(getUserByIdUseCase.execute(999L)).thenThrow(new UserException("User not found", HttpStatus.NOT_FOUND));

            mockMvc.perform(get("/users/{id}", 999)
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andDo(print());

            verify(getUserByIdUseCase, times(1)).execute(999L);
        }
    }

    @Nested
    @DisplayName("Login Cases")
    class LoginUser{
        @Test
        @DisplayName("Login - Success")
        void login_success() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();

            when(loginUserUseCase.execute(loginUserRequest)).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZW1haWwiOiJhZG1pbkBhZG1pbi5jb20iLCJ1c2VybmFtZSI6ImFkbWluIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3NDI0MjA5OTMsImV4cCI6MTc0MjQ1Njk5M30.l43ATfcqSQMMSGIhyl3FbxyoTYeJ9dPlTOfbZBD0BYs");

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());


            verify(loginUserUseCase, times(1)).execute(any(LoginUserRequest.class));
        }

        @Test
        @DisplayName("Login - Error - Blank Email")
        void login_exception_blankEmail() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setEmail("");

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Email can not be empty"));

            verify(loginUserUseCase, never()).execute(any(LoginUserRequest.class));

        }

        @Test
        @DisplayName("Login - Error - Null Email")
        void login_exception_nullEmail() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setEmail(null);

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Email can not be empty"));

            verify(loginUserUseCase, never()).execute(any(LoginUserRequest.class));
        }

        @Test
        @DisplayName("Login - Error - Blank Password")
        void login_exception_blankPassword() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setPassword("");

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Password can not be empty"));

            verify(loginUserUseCase, never()).execute(any(LoginUserRequest.class));

        }

        @Test
        @DisplayName("Login - Error - Null Password")
        void login_exception_nullPassword() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setPassword(null);

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Password can not be empty"));

            verify(loginUserUseCase, never()).execute(any(LoginUserRequest.class));
        }

        @Test
        @DisplayName("Login - Error - Wrong Password")
        void login_exception_wrongPassword() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setPassword("bla");

            when(loginUserUseCase.execute(any(LoginUserRequest.class))).thenThrow(new UserException("Usuário ou senha inválidos", HttpStatus.UNAUTHORIZED));

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos"));

            verify(loginUserUseCase, times(1)).execute(any(LoginUserRequest.class));
        }

        @Test
        @DisplayName("Login - Error - User not found")
        void login_exception_userNotFound() throws Exception {
            LoginUserRequest loginUserRequest = UserTestHelper.generateLoginUserRequest();
            loginUserRequest.setEmail("notfound@notfound.org");

            when(loginUserUseCase.execute(any(LoginUserRequest.class))).thenThrow(new UserException("Usuário não encontrado", HttpStatus.NOT_FOUND));

            mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginUserRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Usuário não encontrado"));

            verify(loginUserUseCase, times(1)).execute(any(LoginUserRequest.class));
        }
    }

    @Nested
    @DisplayName("Change Password Cases")
    class ChangeUserPassword {
        @Test
        @DisplayName("Change Password - Success")
        void changePassword_success() throws Exception {
            Long userId = 1L;
            ChangeUserPasswordRequest passwordRequest = UserTestHelper.generateChangeUserPasswordRequest();

            doNothing().when(changeUserPasswordUseCase).execute(eq(userId), any(ChangeUserPasswordRequest.class));

            mockMvc.perform(put("/users/{id}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(passwordRequest)))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(changeUserPasswordUseCase, times(1)).execute(eq(userId), any(ChangeUserPasswordRequest.class));
        }

        @Test
        @DisplayName("Change Password - error - Wrong Current Password")
        void changePassword_failure_wrongCurrentPassword() throws Exception {
            Long userId = 1L;
            ChangeUserPasswordRequest passwordRequest = UserTestHelper.generateChangeUserPasswordRequest();
            passwordRequest.setCurrentPassword("wrongPassword");

            doThrow(new UserException("Password does not match", HttpStatus.UNAUTHORIZED))
                    .when(changeUserPasswordUseCase).execute(eq(userId), any(ChangeUserPasswordRequest.class));

            mockMvc.perform(put("/users/{id}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(passwordRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Password does not match"));

            verify(changeUserPasswordUseCase, times(1)).execute(eq(userId), any(ChangeUserPasswordRequest.class));
        }

        @Test
        @DisplayName("Change Password - error - User not found")
        void changePassword_failure_userNotFound() throws Exception {
            Long userId = 1L;
            ChangeUserPasswordRequest passwordRequest = UserTestHelper.generateChangeUserPasswordRequest();

            doThrow(new UserException("User not found", HttpStatus.NOT_FOUND))
                    .when(changeUserPasswordUseCase).execute(eq(userId), any(ChangeUserPasswordRequest.class));

            mockMvc.perform(put("/users/{id}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(passwordRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(changeUserPasswordUseCase, times(1)).execute(eq(userId), any(ChangeUserPasswordRequest.class));
        }

        @Test
        @DisplayName("Change Password - error - Internal Server Error")
        void changePassword_failure_internalServerError() throws Exception {
            Long userId = 1L;
            ChangeUserPasswordRequest passwordRequest = UserTestHelper.generateChangeUserPasswordRequest();

            doThrow(new UserException(String.format("Failed to update user with id %d", userId), HttpStatus.INTERNAL_SERVER_ERROR))
                    .when(changeUserPasswordUseCase).execute(eq(userId), any(ChangeUserPasswordRequest.class));

            mockMvc.perform(put("/users/{id}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(passwordRequest)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(String.format("Failed to update user with id %d", userId)));

            verify(changeUserPasswordUseCase, times(1)).execute(eq(userId), any(ChangeUserPasswordRequest.class));
        }
    }

    // Helper method
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
