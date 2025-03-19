package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import com.fiap.GastroHub.shared.infra.crypto.AesCryptoImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UpdateRoleUseCase {
    private static final Logger logger = LogManager.getLogger(UpdateRoleUseCase.class);
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private AesCryptoImp aesCrypto;

    public UpdateRoleUseCase(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.aesCrypto = new AesCryptoImp();
    }

    /**
     * Executes the update role use case
     *
     * @param id Role's id
     * @param request The object with the role's information to be updated
     * @return An object confirming the role's changed information
     **/
    @LogBean
    public Role execute(Long id, Role request) {
        logger.info("Trying to update a role with the following id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));

        role = roleRepository.save(role);
        return role;
    }
}
