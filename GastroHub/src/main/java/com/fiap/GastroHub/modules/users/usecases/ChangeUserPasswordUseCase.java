package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.ChangeUserPasswordRequest;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import com.fiap.GastroHub.shared.infra.crypto.AesCryptoImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class ChangeUserPasswordUseCase {
    private static final Logger logger = LogManager.getLogger(ChangeUserPasswordUseCase.class);

    private final UserRepository userRepository;
    private final AesCryptoImp aesCrypto;

    public ChangeUserPasswordUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.aesCrypto = new AesCryptoImp();
    }


    /**
     * Executes the user password change use case
     *
     * @param id The user's id
     * @param changeUserPasswordRequest Object containing the user info
     **/
    @LogBean
    public void execute(Long id, ChangeUserPasswordRequest changeUserPasswordRequest) {
        logger.info("Trying to change password from user with id: {}", id);

       try {
           User userFromDb = userRepository.findUserById(id).orElse(null);
           if (userFromDb != null) {
               String currentEncrypted = changeUserPasswordRequest.getCurrentPassword();
               if (userFromDb.getPassword().equals(currentEncrypted)) {
                   userFromDb.setPassword(changeUserPasswordRequest.getNewPassword());
                   userFromDb.setLastUpdatedAt(Date.from(Instant.now()));

                   userRepository.save(userFromDb);
               }else{
                   throw new RuntimeException("Password does not match");
               }

           } else {
               throw new RuntimeException("User not found");
           }

           logger.info("Password updated successfully");
       } catch (Exception e) {
           throw new AppException(String.format("Failed to update user with id %d", id), HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }
}
