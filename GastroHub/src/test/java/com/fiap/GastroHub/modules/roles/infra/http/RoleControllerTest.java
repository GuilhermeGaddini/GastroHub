package com.fiap.GastroHub.modules.roles.infra.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.helper.RoleTestHelper;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.usecases.*;
import com.fiap.GastroHub.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Role Controller test Class")
public class RoleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CreateRoleUseCase createRoleUseCase;

    @Mock
    private UpdateRoleUseCase updateRoleUseCase;

    @Mock
    private GetAllRolesUseCase getAllRolesUseCase;

    @Mock
    private GetRoleByIdUseCase getRoleByIdUseCase;

    @Mock
    private DeleteRoleUseCase deleteRoleUseCase;

    @Mock
    private AssignRoleUseCase assignRoleUseCase;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        RoleController roleController = new RoleController(createRoleUseCase, updateRoleUseCase, getAllRolesUseCase, getRoleByIdUseCase, deleteRoleUseCase, assignRoleUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(roleController)
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
    class CreateRole {

        @Test
        @DisplayName("Create Role - Success")
        void createRole_success() throws Exception {
            CreateUpdateRoleRequest roleRequest = RoleTestHelper.generateCreateUpdateRoleRequest();
            Role role = RoleTestHelper.generateFullRole();

            when(createRoleUseCase.execute(roleRequest)).thenReturn(role);

            mockMvc.perform(post("/roles/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(roleRequest)))
                    .andExpect(status().isOk());

            verify(createRoleUseCase, times(1)).execute(any(CreateUpdateRoleRequest.class));
        }

        @Test
        @DisplayName("Create Role - Error - Blank Name")
        void createRole_exception_blankName() throws Exception {
            var roleRequest = new CreateUpdateRoleRequest();
            roleRequest.setName("");

            mockMvc.perform(post("/roles/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(roleRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"));

            verify(createRoleUseCase, never()).execute(any(CreateUpdateRoleRequest.class));

        }

        @Test
        @DisplayName("Create Role - Error - Null Name")
        void createRole_exception_nullName() throws Exception{
            var roleRequest = new CreateUpdateRoleRequest();
            roleRequest.setName(null);

            mockMvc.perform(post("/roles/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(roleRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"));

            verify(createRoleUseCase, never()).execute(any(CreateUpdateRoleRequest.class));
        }

        @Test
        @DisplayName("Create Role - Error - XML Payload")
        void createRole_exception_xmlPayload() throws Exception{
            String xmlPayload = "<role><id>2</id><name>admin_role</name></role>";

            mockMvc.perform(post("/roles/create")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(createRoleUseCase, never()).execute(any(CreateUpdateRoleRequest.class));
        }

    }

    @Nested
    @DisplayName("Get cases")
    class GetRoles {

        @Test
        @DisplayName("Get all Cases")
        void getAllRoles_success() throws Exception {
            when(getAllRolesUseCase.execute()).thenReturn(List.of(RoleTestHelper.generateFullRole()));

            mockMvc.perform(get("/roles")
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andDo(print());

            verify(getAllRolesUseCase, times(1)).execute();
        }

        @Test
        @DisplayName("Get Role By ID - Success")
        void getRoleById_success() throws Exception {
            Role role = RoleTestHelper.generateFullRole();
            when(getRoleByIdUseCase.execute(1L)).thenReturn(role);

            mockMvc.perform(get("/roles/{id}", 1)
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(role.getId()))
                    .andExpect(jsonPath("$.name").value(role.getName()))
                    .andDo(print());

            verify(getRoleByIdUseCase, times(1)).execute(1L);
        }

        @Test
        @DisplayName("Get Role By ID - Error - ID don't exist")
        void getRoleById_exception_idDontExist() throws Exception {
            when(getRoleByIdUseCase.execute(999L)).thenThrow(new RoleException("Role not found", HttpStatus.NOT_FOUND));

            mockMvc.perform(get("/roles/{id}", 999)
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Role not found"))
                    .andDo(print());

            verify(getRoleByIdUseCase, times(1)).execute(999L);
        }
    }

    @Nested
    @DisplayName("Update Cases")
    class UpdateRole {

        @Test
        @DisplayName("Update - Success")
        void updateRole_success() throws Exception {
            CreateUpdateRoleRequest updatedRole = RoleTestHelper.generateCreateUpdateRoleRequest();
            Role role = RoleTestHelper.generateFullRole();

            when(updateRoleUseCase.execute(eq(1L), any(CreateUpdateRoleRequest.class))).thenReturn(role);

            mockMvc.perform(put("/roles/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updatedRole)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value(updatedRole.getName()))
                    .andDo(print());

            verify(updateRoleUseCase, times(1)).execute(eq(1L), any(CreateUpdateRoleRequest.class));
        }

        @Test
        @DisplayName("Update - Error - Blank Name")
        void updateRole_exception_blankName() throws Exception {
            CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
            roleRequest.setName("");

            mockMvc.perform(put("/roles/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(roleRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"))
                    .andDo(print());

            verify(updateRoleUseCase, never()).execute(eq(1L), any(CreateUpdateRoleRequest.class));
        }

        @Test
        @DisplayName("Update - Error - Null Name")
        void updateRole_exception_nullName() throws Exception {
            Role roleRequest = new Role();
            roleRequest.setName(null);

            mockMvc.perform(put("/roles/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(roleRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"))
                    .andDo(print());

            verify(updateRoleUseCase, never()).execute(eq(1L), any(CreateUpdateRoleRequest.class));
        }
    }

    @Nested
    @DisplayName("Delete Cases")
    class DeleteRole {

        @Test
        @DisplayName("Delete - Success")
        void deleteRole_success() throws Exception {
            doNothing().when(deleteRoleUseCase).execute(1L);

            mockMvc.perform(delete("/roles/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            verify(deleteRoleUseCase, times(1)).execute(1L);
        }

        @Test
        @DisplayName("Delete - Error - ID don't exist")
        void deleteRole_exception_idDontExist() throws Exception {
            doThrow(new RoleException("Role not found", HttpStatus.NOT_FOUND))
                    .when(deleteRoleUseCase).execute(999L);

            mockMvc.perform(delete("/roles/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Role not found"))
                    .andDo(print());

            verify(deleteRoleUseCase, times(1)).execute(999L);
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

