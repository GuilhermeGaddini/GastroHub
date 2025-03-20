package com.fiap.GastroHub.roles.service;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.roles.usecases.UpdateRoleUseCase;
import com.fiap.GastroHub.shared.infra.crypto.AesCryptoImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AesCryptoImp aesCrypto; // Embora não esteja sendo usado no código fornecido, mantive a declaração

    @InjectMocks
    private UpdateRoleUseCase updateRoleUseCase;

    private Role existingRole;
    private Role updatedRoleRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        existingRole = new Role();
        existingRole.setId(1L);
        existingRole.setName("Old Admin");

        updatedRoleRequest = new Role();
        updatedRoleRequest.setName("New Admin");
    }

    @Test
    void execute_ValidIdAndRequest_UpdatesAndReturnsRole() {
        var role = new Role();
        role.setId(Long.valueOf(1));
        role.setName("New Admin");

        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        Role result = updateRoleUseCase.execute(1L, updatedRoleRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Admin", result.getName());

        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void execute_RoleNotFound_ThrowsRoleException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleException exception = assertThrows(RoleException.class, () -> updateRoleUseCase.execute(1L, updatedRoleRequest));

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, never()).save(any(Role.class));
    }
}