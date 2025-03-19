package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.dtos.AssignRoleRequest;
import com.fiap.GastroHub.modules.roles.exceptions.RoleException;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AssignRoleUseCase {
    private static final Logger logger = LogManager.getLogger(UpdateRoleUseCase.class);
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AssignRoleUseCase(RoleRepository roleRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executes the assignment of a role to a given user
     *
     * @param request The object with the role's information to be updated
     * @return An object confirming the role's changed information
     **/
    @LogBean
    public User execute(AssignRoleRequest request) {
        logger.info("Trying to assing the role with id {} to the user with is {}", request.getRoleId(), request.getUserId());

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RoleException("User not found", HttpStatus.NOT_FOUND));
        user.setRole(role);
        user = userRepository.save(user);
        return user;
    }
}
