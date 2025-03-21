package com.fiap.GastroHub.modules.users.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest {
    @NotEmpty(message = "Email can not be empty")
    private String email;
    @NotEmpty(message = "Password can not be empty")
    private String password;
}