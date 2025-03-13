package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByIdUseCase {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get user use case
     *
     * @param id User's id
     * @return An objetc containing the user's information
     **/
    @LogBean
    public UserResponse execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.BAD_REQUEST));
        return modelMapper.map(user, UserResponse.class);
    }

}
