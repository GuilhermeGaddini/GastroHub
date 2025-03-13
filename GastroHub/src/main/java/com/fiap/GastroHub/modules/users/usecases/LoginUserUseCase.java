package com.fiap.GastroHub.modules.users.usecases;

import com.fiap.GastroHub.modules.users.dtos.LoginUserRequest;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.util.JwtUtil;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoginUserUseCase {
    private static final Logger logger = LogManager.getLogger(LoginUserUseCase.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LoginUserUseCase(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Executes the user login use case
     *
     * @param loginUserRequest An object with the login information for the specific user
     * @return A JWT token
     **/
    @LogBean
    public String execute(LoginUserRequest loginUserRequest) {
        logger.info("Iniciando o processo de login para o email: {}", loginUserRequest.getEmail());

        User user = userRepository.findUserByEmail(loginUserRequest.getEmail())
                .orElseThrow(() -> new AppException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        logger.info("Usuário encontrado: {}", user.getName());

        if (user.getPassword().equals(loginUserRequest.getPassword())) {
            logger.info("Senha válida para o usuário: {}", user.getName());

            logger.info("Token gerado com sucesso para o usuário: {}", user.getName());
            return jwtUtil.generateToken(user.getId(), user.getName(), user.getEmail());
        } else {
            throw new AppException("Usuário ou senha inválidos", HttpStatus.UNAUTHORIZED);
        }
    }
}
