package com.fiap.GastroHub.modules.restaurants.dtos;

import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import lombok.*;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private String email;
    private String cuisineType;
    private String openingHours;
    private User owner;
}
