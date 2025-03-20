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
    @DisplayName("Get all users with success")
    void getAllUsers() {
        List<User> users = List.of();
        when(userRepository.findAll()).thenReturn(users);

        var storedUsers = userRepository.findAll();
        verify(userRepository, times(1)).findAll();
        assertThat(storedUsers).isEqualTo(users);
    }

    @Test
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
    void deleteUser() {
        var id = 1L;

        doNothing().when(userRepository).deleteById(any(Long.class));
        userRepository.deleteById(id);
        verify(userRepository, times(1)).deleteById(id);
    }
}
