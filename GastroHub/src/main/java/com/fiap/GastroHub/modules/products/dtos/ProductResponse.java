package com.fiap.GastroHub.modules.products.dtos;

import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import lombok.*;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private String name;
    private BigDecimal price;
    private String availability;
    private String picPath;
    private Restaurant restaurant;
}
