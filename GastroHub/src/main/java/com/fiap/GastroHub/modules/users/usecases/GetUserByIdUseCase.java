package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.exceptions.UserException;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get user use case
     *
     * @param id User's id
     * @return An object containing the user's information
     **/
    @LogBean
    public UserResponse execute(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.BAD_REQUEST));
            return modelMapper.map(user, UserResponse.class);
        } catch (Exception e) {
            throw new UserException("User not found", HttpStatus.BAD_REQUEST);
        }
    }

}
