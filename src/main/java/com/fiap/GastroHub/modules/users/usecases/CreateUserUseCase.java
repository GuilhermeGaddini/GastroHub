package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.CreateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.CreateUserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class CreateUserUseCase {
    private static final Logger logger = LogManager.getLogger(CreateUserUseCase.class);

    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Executes the user creation use case
     * @param createUserRequest Object containing the user info
     * @return Response object with user created successfully
     **/
    @LogBean
    public CreateUserResponse execute(CreateUserRequest createUserRequest) {
        logger.info("Trying to create a new user with the following info: {}", createUserRequest.toString());
        try{
            User newUser = new User();
            newUser.setName(createUserRequest.getName());
            newUser.setUsername(createUserRequest.getUsername());
            newUser.setAddress(createUserRequest.getAddress());
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setPassword(createUserRequest.getPassword());
            newUser.setCreatedAt(Date.from(Instant.now()));
            newUser.setLastUpdatedAt(Date.from(Instant.now()));

            User createdUser = userRepository.save(newUser);

            logger.info("New user created successfully");

            return new CreateUserResponse(createdUser.getId(), createdUser.getUsername(), createdUser.getEmail());
        } catch (Exception e) {
            throw new AppException("Failed to create a new user", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
