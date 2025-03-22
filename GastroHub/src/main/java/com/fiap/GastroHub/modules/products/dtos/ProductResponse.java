package com.fiap.GastroHub.modules.products.dtos;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String name;
    private BigDecimal price;
    private String availability;
    private String picPath;
}
