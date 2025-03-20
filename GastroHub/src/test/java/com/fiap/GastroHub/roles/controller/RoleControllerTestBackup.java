package com.fiap.GastroHub.roles.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.infra.http.RoleController;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.usecases.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateRoleUseCase createRoleUseCase;

    @MockBean
    private UpdateRoleUseCase updateRoleUseCase;

    @MockBean
    private GetAllRolesUseCase getAllRolesUseCase;

    @MockBean
    private GetRoleByIdUseCase getRoleByIdUseCase;

    @MockBean
    private DeleteRoleUseCase deleteRoleUseCase;

    @MockBean
    private AssignRoleUseCase assignRoleUseCase;

    @Test
    void createRole_ShouldReturnCreatedRole() throws Exception {
        CreateUpdateRoleRequest request = new CreateUpdateRoleRequest();
        request.setName("ADMIN");

        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("ADMIN");

        Mockito.when(createRoleUseCase.execute(request)).thenReturn(mockRole);

        mockMvc.perform(post("/roles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ADMIN")));
    }

    @Test
    void getAllRoles_ShouldReturnRolesList() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        List<Role> roles = Collections.singletonList(role);

        Mockito.when(getAllRolesUseCase.execute()).thenReturn(roles);

        mockMvc.perform(get("/roles")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("USER")));
    }

    @Test
    void getRoleById_ShouldReturnRole() throws Exception {
        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("ADMIN");

        Mockito.when(getRoleByIdUseCase.execute(1L)).thenReturn(mockRole);

        mockMvc.perform(get("/roles/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ADMIN")));
    }

    @Test
    void updateRole_ShouldReturnUpdatedRole() throws Exception {
        Role request = new Role();
        request.setName("UPDATED_ADMIN");

        Role updatedRole = new Role();
        updatedRole.setId(1L);
        updatedRole.setName("UPDATED_ADMIN");

        Mockito.when(updateRoleUseCase.execute(1L, request)).thenReturn(updatedRole);

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("UPDATED_ADMIN")));
    }

    @Test
    void deleteRole_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(deleteRoleUseCase, Mockito.times(1)).execute(1L);
    }

    @Test
    void createRole_ShouldValidateEmptyName() throws Exception {
        CreateUpdateRoleRequest invalidRequest = new CreateUpdateRoleRequest();
        invalidRequest.setName("");

        mockMvc.perform(post("/roles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRoleById_ShouldHandleNotFound() throws Exception {
        Mockito.when(getRoleByIdUseCase.execute(99L))
                .thenThrow(new RuntimeException("Role not found"));

        mockMvc.perform(get("/roles/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }
}