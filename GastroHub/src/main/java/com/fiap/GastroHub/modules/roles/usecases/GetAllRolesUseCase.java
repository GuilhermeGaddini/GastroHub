package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase {
    private final RoleRepository roleRepository;

    /**
     * Executes the get all roles use case
     *
     * @return A list containing information on all roles
     **/
    @LogBean
    public List<Role> execute() {
        try {
            return roleRepository.findAll().stream().toList();
        } catch (RuntimeException e) {
            throw new RoleException("Error fetching roles", HttpStatus.BAD_REQUEST);
        }
    }
}
