package com.fiap.GastroHub.modules.users.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private long id;
    private String name;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
