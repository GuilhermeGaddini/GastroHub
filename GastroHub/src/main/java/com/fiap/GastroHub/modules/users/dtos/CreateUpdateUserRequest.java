package com.fiap.GastroHub.modules.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateUserRequest {
    @NotBlank(message = "Name can not be empty")
    private String name;

    @NotBlank(message = "Address can not be empty")
    private String address;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password can not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
