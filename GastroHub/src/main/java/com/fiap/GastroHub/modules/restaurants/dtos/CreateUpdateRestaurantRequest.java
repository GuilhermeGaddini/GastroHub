package com.fiap.GastroHub.modules.restaurants.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
public class CreateUpdateRestaurantRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;

    @NotBlank(message = "Opening hours is required")
    private String openingHours;

    @NotBlank(message = "Owner ID is required")
    private Long owner;

}
