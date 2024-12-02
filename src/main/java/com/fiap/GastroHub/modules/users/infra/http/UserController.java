package com.fiap.GastroHub.modules.users.infra.http;

import com.fiap.GastroHub.modules.users.dtos.CreateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.CreateUserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.modules.users.usecases.CreateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserRepository userRepository;
    private final CreateUserUseCase createUserUseCase;

    public UserController(UserRepository userRepository, CreateUserUseCase createUserUseCase) {
        this.userRepository = userRepository;
        this.createUserUseCase = createUserUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        return new ResponseEntity<>(userRepository.findUserById(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return new ResponseEntity<>(createUserUseCase.execute(createUserRequest), HttpStatus.OK);
    }
}
