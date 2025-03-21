package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.ChangeUserPasswordRequest;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
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
           User userFromDb = userRepository.findById(id).orElse(null);
           if (userFromDb != null) {
               String currentEncrypted = changeUserPasswordRequest.getCurrentPassword();
               if (userFromDb.getPassword().equals(currentEncrypted)) {
                   userFromDb.setPassword(changeUserPasswordRequest.getNewPassword());
                   userFromDb.setLastUpdatedAt(Date.from(Instant.now()));

                   userRepository.save(userFromDb);
               }else{
                   throw new UserException("Password does not match", HttpStatus.UNAUTHORIZED);
               }

           } else {
               throw new UserException("User not found", HttpStatus.NOT_FOUND);
           }

           logger.info("Password updated successfully");
       }catch (UserException e) {
           // Exceções específicas
           throw e;
       } catch (Exception e) {
           throw new UserException(String.format("Failed to update user with id %d", id), HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }
}
