package com.fiap.GastroHub.modules.roles.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignRoleRequest {
    @NotBlank(message = "Role Id is required")
    private Long roleId;

    @NotBlank(message = "User Id is required")
    private Long userId;
}
