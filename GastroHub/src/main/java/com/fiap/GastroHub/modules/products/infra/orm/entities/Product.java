package com.fiap.GastroHub.modules.products.infra.orm.entities;

import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, unique = true)
    private String availability;

    @Column(nullable = false)
    private String picPath;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Restaurant restaurant;
}
