package com.fiap.GastroHub.modules.restaurants.infra.orm.repositories;

import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
    Optional<Restaurant> findByName(String name);
}
