package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import com.fiap.GastroHub.shared.infra.crypto.AesCryptoImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserUseCase {
    private static final Logger logger = LogManager.getLogger(UpdateUserUseCase.class);
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private AesCryptoImp aesCrypto;

    public UpdateUserUseCase(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.aesCrypto = new AesCryptoImp();
    }

    /**
     * Executes the update user use case
     *
     * @param id User's id
     * @param request The object with the user's information to be updated
     * @return An object confirming the user's changed information
     **/
    @LogBean
    public UserResponse execute(Long id, CreateUpdateUserRequest request) {
        logger.info("Trying to update a user with the following id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        modelMapper.map(request, user);
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }
}
