package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRestaurantMenuUseCase {

    private final RestaurantRepository restaurantRepository;

    /**
     * Executes the get restaurant menu use case
     *
     * @param id Restaurant's id
     * @return An object containing the restaurant's menu list of products
     **/
    @LogBean
    public List<Product> execute(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException("Restaurant not found", HttpStatus.BAD_REQUEST));
        return restaurant.getProducts();
    }
}
