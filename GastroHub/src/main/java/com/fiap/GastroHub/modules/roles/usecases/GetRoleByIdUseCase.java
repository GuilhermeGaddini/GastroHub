package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoleByIdUseCase {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get role use case
     *
     * @param id Role's id
     * @return An objetc containing the role's information
     **/
    @LogBean
    public Role execute(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("Role not found", HttpStatus.BAD_REQUEST));
        return role;
    }

}
