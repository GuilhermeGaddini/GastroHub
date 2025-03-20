package com.fiap.GastroHub.roles.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.helper.RoleTestHelper;
import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.http.RoleController;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.usecases.*;
import com.fiap.GastroHub.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@AutoConfigureMockMvc(addFilters = false)
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
    class CreateRole{

        @Test
        void createRole_success() throws Exception{
            CreateUpdateRoleRequest roleRequest = RoleTestHelper.generateCreateUpdateRoleRequest();
            Role role = RoleTestHelper.generateFullRole();

            when(createRoleUseCase.execute(roleRequest)).thenReturn(role);

            mockMvc.perform(post("/roles/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(roleRequest)))
                    .andExpect(status().isOk());

            verify(createRoleUseCase, times(1)).execute(any(CreateUpdateRoleRequest.class));
        }

        @Test
        void createRole_exception_blankName() throws Exception{
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
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
