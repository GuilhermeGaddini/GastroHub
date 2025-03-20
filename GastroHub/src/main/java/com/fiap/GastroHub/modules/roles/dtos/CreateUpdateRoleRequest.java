package com.fiap.GastroHub.modules.roles.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateRoleRequest {
    @NotBlank(message = "Name can not be empty")
    private String name;
}
