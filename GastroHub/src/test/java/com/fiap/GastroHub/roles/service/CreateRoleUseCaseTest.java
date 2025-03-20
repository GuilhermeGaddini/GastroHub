package com.fiap.GastroHub.roles.service;

import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.CreateRoleUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CreateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CreateRoleUseCase createRoleUseCase;


    private Role roleEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roleEntity = new Role();
        roleEntity.setId(1L);
        roleEntity.setName("Admin");
    }

    @Test
    void execute_ValidRequest_CreatesAndReturnsRole() {

//        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
//        roleRequest.setName("Admin");
//
//        Role hardcoded = new Role();
//        hardcoded.setId(1L);
//        hardcoded.setName("Admin");
//
//        when(modelMapper.map(roleRequest, Role.class)).thenReturn(hardcoded);
//        when(roleRepository.save(hardcoded)).thenReturn(hardcoded);
//
//        Role result = createRoleUseCase.execute(roleRequest);
//
//        assertNotNull(result);
//        assertEquals(roleEntity.getId(), result.getId());
//        assertEquals(roleEntity.getName(), result.getName());
//        verify(roleRepository, times(1)).save(roleEntity);

        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
        roleRequest.setName("Admin");

        Role mappedRole = new Role();
        mappedRole.setName("Admin");

        when(modelMapper.map(roleRequest, Role.class)).thenReturn(mappedRole);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        // Act
        Role result = createRoleUseCase.execute(roleRequest);

        // Assert
        assertNotNull(result);
        assertEquals(roleEntity.getId(), result.getId());
        assertEquals(roleEntity.getName(), result.getName());
        verify(roleRepository, times(1)).save(mappedRole); // Verifica se o método save foi chamado com o objeto correto
    }

    @Test
    void execute_RepositoryThrowsException_ThrowsRoleException() {
        CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
        // roleRequest.setName("Admin");

        when(modelMapper.map(roleRequest, Role.class)).thenReturn(roleEntity);
        when(roleRepository.save(any(Role.class))).thenThrow(new RuntimeException("Database error"));

        RoleException exception = assertThrows(RoleException.class, () -> createRoleUseCase.execute(roleRequest));

        assertEquals("An unexpected error occurred while creating the role.", exception.getMessage());
        verify(roleRepository, times(1)).save(any(Role.class));
    }
}