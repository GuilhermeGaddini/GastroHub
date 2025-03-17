package com.fiap.GastroHub.modules.products.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;


@Data
public class CreateUpdateProductRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Price type is required")
    private BigDecimal price;

    @NotBlank(message = "Availability is required")
    private String availability;

    private String picPath;

    @NotBlank(message = "Restaurant ID is required")
    private Long restaurant;

}
