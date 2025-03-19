package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoleByIdUseCase {
    private final RoleRepository roleRepository;

    /**
     * Executes the get role use case
     *
     * @param id Role's id
     * @return An object containing the role's information
     **/
    @LogBean
    public Role execute(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new RoleException("Role not found", HttpStatus.BAD_REQUEST));
    }

}
