package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase {
    private final RoleRepository roleRepository;

    /**
     * Executes the role deletion use case
     *
     * @param id Role's id
     **/
    @LogBean
    public void execute(Long id) {
        if (id == null) { throw new RoleException("Role with ID null not allowed", HttpStatus.BAD_REQUEST); }

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role with ID " + id + " not found", HttpStatus.BAD_REQUEST));

        try {
            roleRepository.delete(role);
        } catch (Exception e) {
            throw new RoleException("Error Deleting role", HttpStatus.BAD_REQUEST);
        }
    }
}
