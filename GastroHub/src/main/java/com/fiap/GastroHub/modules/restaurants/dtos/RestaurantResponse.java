package com.fiap.GastroHub.modules.restaurants.dtos;

import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private String cuisineType;
    private String openingHours;
    private UserResponse owner;
}
