package com.fiap.GastroHub.modules.products.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
public class CreateUpdateProductRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;

    @NotBlank(message = "Restaurant ID is required")
    private Long restaurantId;

}
