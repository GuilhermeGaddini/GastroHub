package com.fiap.GastroHub.modules.products.infra.orm.repositories;

import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long aLong);
    Optional<Product> findByName(String name);
    Optional<Product> findProductByRestaurantId(Long restaurantId);
}
