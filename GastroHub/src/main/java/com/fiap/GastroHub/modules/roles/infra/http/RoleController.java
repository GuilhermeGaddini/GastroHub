package com.fiap.GastroHub.modules.roles.infra.http;


import com.fiap.GastroHub.modules.roles.dtos.AssignRoleRequest;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.usecases.*;
import com.fiap.GastroHub.modules.users.dtos.LoginUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roles")
public class RoleController {
    private static final Logger logger = LogManager.getLogger(RoleController.class);

    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final GetAllRolesUseCase getAllRolesUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final AssignRoleUseCase assignRoleUseCase;

    public RoleController(CreateRoleUseCase createRoleUseCase,
                          UpdateRoleUseCase updateRoleUseCase,
                          GetAllRolesUseCase getAllRolesUseCase,
                          GetRoleByIdUseCase getRoleByIdUseCase,
                          DeleteRoleUseCase deleteRoleUseCase,
                          AssignRoleUseCase assignRoleUseCase) {

        this.createRoleUseCase = createRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.getAllRolesUseCase = getAllRolesUseCase;
        this.getRoleByIdUseCase = getRoleByIdUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
    }

    @Operation(summary = "Criar um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PostMapping("/create")
    public ResponseEntity<Role> createRole(
            @RequestBody Role request
    ) {
        Role createdRole = createRoleUseCase.execute(request);
        return ResponseEntity.ok(createdRole);
    }

    @Operation(summary = "Obter informações de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        logger.info("/roles");
        List<Role> roles = getAllRolesUseCase.execute();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obter informações de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@RequestHeader("Authorization") String token, @PathVariable long id) {

        Role roleResponse = getRoleByIdUseCase.execute(id);
        return ResponseEntity.ok(roleResponse);
    }

    @Operation(summary = "Atualizar informações de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(
            @PathVariable("id") Long id,
            @RequestBody Role request
    ) {
        logger.info("PUT -> /roles/{}", id);
        Role updatedRole = updateRoleUseCase.execute(id, request);
        return ResponseEntity.ok(updatedRole);
    }


    @Operation(summary = "Deletar uma role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {
        logger.info("DELETE -> /roles/{}", id);
        deleteRoleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atribui uma role a um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PostMapping("/assign")
    public ResponseEntity<Void> assignRoleToUser(@RequestBody AssignRoleRequest assignRoleRequest) {

        String token = loginUserUseCase.execute(loginUserRequest);
        LoginUserResponse response = new LoginUserResponse(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
