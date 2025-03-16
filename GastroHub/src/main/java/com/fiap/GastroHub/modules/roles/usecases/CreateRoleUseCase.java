package com.fiap.GastroHub.modules.roles.usecases;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import com.fiap.GastroHub.shared.infra.crypto.AesCryptoImp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {
    private static final Logger logger = LogManager.getLogger(CreateRoleUseCase.class);
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private AesCryptoImp aesCrypto; //TODO: implementar a senha criptografada.

    /**
     * Executes the role creation use case
     *
     * @param request Object containing the role info
     * @return Response object with role created successfully
     **/
    @LogBean
    @Transactional
    public Role execute(Role request) {
        logger.info("Trying to create a new role with the following info: {}", request.getName());

        try {
            Role role = modelMapper.map(request, Role.class);
            roleRepository.save(role);
            logger.info("New role created successfully");
            return role;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new AppException("An unexpected error occurred while creating the role.", HttpStatus.BAD_REQUEST);
        }
    }
}
