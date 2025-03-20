package com.fiap.GastroHub.roles.service;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.CreateRoleUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    private ModelMapper modelMapper;

    AutoCloseable mock;
    private CreateRoleUseCase createRoleUseCase;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        modelMapper = Mockito.spy(new ModelMapper());
        createRoleUseCase = new CreateRoleUseCase(roleRepository, modelMapper);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    void execute_ValidRequest_CreatesAndReturnsRole() {
        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
        roleRequest.setName("Admin");

        Role role = new Role();
        role.setName("Admin");

        Role roleEntity = new Role();
        roleEntity.setId(1L);
        roleEntity.setName("Admin");

//        when(modelMapper.map(any(CreateUpdateRoleRequest.class), eq(Role.class))).thenReturn(roleEntity);
        doReturn(roleEntity)
                .when(modelMapper)
                .map(any(CreateUpdateRoleRequest.class), eq(Role.class));
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        Role result = createRoleUseCase.execute(roleRequest);

        assertNotNull(result);
        assertEquals(roleEntity.getId(), result.getId());
        assertEquals(roleEntity.getName(), result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void execute_RepositoryThrowsException_ThrowsRoleException() {
        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
        roleRequest.setName("Admin");

        Role roleEntity = new Role();
        roleEntity.setId(1L);
        roleEntity.setName("Admin");

        when(modelMapper.map(roleRequest, Role.class)).thenReturn(roleEntity);
        when(roleRepository.save(any(Role.class))).thenThrow(new RuntimeException("Database error"));

        RoleException exception = assertThrows(RoleException.class, () -> createRoleUseCase.execute(roleRequest));

        assertEquals("An unexpected error occurred while creating the role.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(roleRepository, times(1)).save(any(Role.class));
    }
}