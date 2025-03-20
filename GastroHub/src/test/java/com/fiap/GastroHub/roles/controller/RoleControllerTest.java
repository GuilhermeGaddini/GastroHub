package com.fiap.GastroHub.roles.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.helper.RoleTestHelper;
import com.fiap.GastroHub.modules.roles.infra.http.RoleController;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.usecases.*;
import com.fiap.GastroHub.shared.infra.http.handlers.AppExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

//    @BeforeEach
//    void setUp() {
//        mock = MockitoAnnotations.openMocks(this);
//        RoleController roleController = new RoleController(createRoleUseCase, updateRoleUseCase, getAllRolesUseCase, getRoleByIdUseCase, deleteRoleUseCase, assignRoleUseCase);
//        mockMvc = MockMvcBuilders.standaloneSetup(roleController)
//                .setControllerAdvice(new AppExceptionHandler())
//                .addFilter((request, response, chain) -> {
//                    response.setCharacterEncoding("UTF-8");
//                    chain.doFilter(request, response);
//                }, "/*")
//                .build();
//    }
//
//    @AfterEach
//    void teardown() throws Exception {
//        mock.close();
//    }
//
//    @Nested
//    class CreateRole{
//
//        @Test
//        void createRole_success() throws Exception{
//            var roleRequest = RoleTestHelper.generateRole();
//
//            when(createRoleUseCase.execute(any(Role.class))).thenAnswer(i -> i.getArgument(0));
//
//            mockMvc.perform(post("/roles").contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(roleRequest)))
//                    .andExpect(status().isCreated());
//
//            verify(createRoleUseCase, times(1)).execute(any(Role.class));
//        }
//
//        @Test
//        void createRole_exception_blankName() throws Exception{
//            var roleRequest = RoleTestHelper.generateRole();
//            roleRequest.setName("");
//
//            mockMvc.perform(post("/roles").contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(roleRequest)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.message").value("Validation error"))
//                    .andExpect(jsonPath("$.errors[0]").value("Name can not be empty"));
//
//            verify(createRoleUseCase, never()).execute(any(Role.class));
//
//        }
//
//        @Test
//        void createRole_exception_nullName() throws Exception{
//            var roleRequest = RoleTestHelper.generateRole();
//            roleRequest.setName(null);
//
//            mockMvc.perform(post("/roles").contentType(MediaType.APPLICATION_JSON)
//                            .content(asJsonString(roleRequest)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.message").value("Validation error"))
//                    .andExpect(jsonPath("$.errors[0]").value("Name can not be null"));
//
//            verify(createRoleUseCase, never()).execute(any(Role.class));
//        }
//
//        @Test
//        void createRole_exception_xmlPayload() throws Exception{
//            String xmlPayload = "<role><id>2</id><name>admin_role</name></role>";
//
//            mockMvc.perform(post("/roles")
//                            .contentType(MediaType.APPLICATION_XML)
//                            .content(xmlPayload))
//                    .andExpect(status().isMethodNotAllowed());
//            verify(createRoleUseCase, never()).execute(any(Role.class));
//        }
//    }
//
//
//    @Nested
//    class GetRoles{
//
//        @Test
//        void getAllRoles_success() throws Exception{}
//
//        @Test
//        void getRoleById_success() throws Exception{}
//
//        @Test
//        void getRoleById_exception_idDontExist() throws Exception{}
//
//        @Test
//        void deleteRole_exception_nullId() throws Exception{}
//
//    }
//
//
//    @Nested
//    class UpdateRole{
//        @Test
//        void updateRole_success() throws Exception{
//
//        }
//
//        @Test
//        void updateRole_exception_blankName() throws Exception{
//
//        }
//
//        @Test
//        void updateRole_exception_nullName() throws Exception{
//
//        }
//
//        @Test
//        void updateRole_exception_invalidId() throws Exception{
//
//        }
//
//        @Test
//        void updateRole_exception_xmlPayload() throws Exception{
//
//        }
//    }
//
//
//    @Nested
//    class DeleteRole{
//        @Test
//        void deleteRole_success() throws Exception{
//
//        }
//
//        @Test
//        void deleteRole_exception_idDontExist() throws Exception{}
//
//        @Test
//        void deleteRole_exception_nullId() throws Exception{}
//    }
//
//    private String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
