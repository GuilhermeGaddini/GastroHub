package com.fiap.GastroHub.users.repository;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Repository Test Class")
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

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
    @DisplayName("Create - Success")
    void createUser() {
        var role = new Role(1L, "Admin");
        var user = new User();
        user.setName("John Doe");
        user.setAddress("123 Main Street");
        user.setEmail("johndoe@example.com");
        user.setPassword("securepassword");
        user.setRole(role);

        when(userRepository.save(any(User.class))).thenReturn(user);

        var storedUser = userRepository.save(user);
        verify(userRepository, times(1)).save(user);
        assertThat(storedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Get all users - Success")
    void getAllUsers() {
        List<User> users = List.of();
        when(userRepository.findAll()).thenReturn(users);

        var storedUsers = userRepository.findAll();
        verify(userRepository, times(1)).findAll();
        assertThat(storedUsers).isEqualTo(users);
    }

    @Test
    @DisplayName("Get By ID - Success")
    void getUserById() {
        var id = 1L;
        var role = new Role(1L, "Admin");
        var user = new User();
        user.setId(id);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setRole(role);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        var storedUser = userRepository.findById(id);
        verify(userRepository, times(1)).findById(id);
        assertThat(storedUser).isNotNull().containsSame(user);
    }

    @Test
    @DisplayName("Get By Email - Success")
    void getUserByEmail() {
        var role = new Role(1L, "Admin");
        var user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setRole(role);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        var storedUser = userRepository.findByEmail(user.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        assertThat(storedUser).isNotNull().containsSame(user);
    }

    @Test
    @DisplayName("Delete - Success")
    void deleteUser() {
        var id = 1L;

        doNothing().when(userRepository).deleteById(any(Long.class));
        userRepository.deleteById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Create - Failure: Saving null user")
    void createUserFailure() {
        when(userRepository.save(any(User.class))).thenThrow(new IllegalArgumentException("User cannot be null"));

        try {
            userRepository.save(null);
        } catch (IllegalArgumentException e) {
            verify(userRepository, times(1)).save(null);
            assertThat(e.getMessage()).isEqualTo("User cannot be null");
        }
    }

    @Test
    @DisplayName("Get By ID - Error - Non-existent ID")
    void getUserByIdFailure() {
        var id = 99L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        var storedUser = userRepository.findById(id);
        verify(userRepository, times(1)).findById(id);
        assertThat(storedUser).isEmpty();
    }

    @Test
    @DisplayName("Get By Email - Error - Non-existent Email")
    void getUserByEmailFailure() {
        var email = "nonexistent@example.com";
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        var storedUser = userRepository.findByEmail(email);
        verify(userRepository, times(1)).findByEmail(email);
        assertThat(storedUser).isEmpty();
    }

    @Test
    @DisplayName("Delete - Error - Non-existent ID")
    void deleteUserFailure() {
        var id = 99L;
        doThrow(new IllegalArgumentException("ID not found")).when(userRepository).deleteById(any(Long.class));

        try {
            userRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            verify(userRepository, times(1)).deleteById(id);
            assertThat(e.getMessage()).isEqualTo("ID not found");
        }
    }
}
