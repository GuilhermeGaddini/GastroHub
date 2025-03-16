package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.AppException;
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
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("Role with ID " + id + " not found", HttpStatus.BAD_REQUEST));
        roleRepository.delete(role);
    }
}
