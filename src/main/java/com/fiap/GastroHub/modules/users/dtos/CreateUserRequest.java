package com.fiap.GastroHub.modules.users.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateUserRequest {
    private String name;
    private String username;
    private String address;
    private String email;
    private String password;
}
