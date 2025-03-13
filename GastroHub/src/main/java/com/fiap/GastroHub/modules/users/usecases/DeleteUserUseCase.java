package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {
    private final UserRepository userRepository;

    /**
     * Executes the user deletion use case
     *
     * @param id User's id
     **/
    @LogBean
    public void execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User with ID " + id + " not found", HttpStatus.BAD_REQUEST));
        userRepository.delete(user);
    }
}
