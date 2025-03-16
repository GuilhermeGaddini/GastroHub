package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRestaurantMenuUseCase {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get restaurant menu use case
     *
     * @param id Restaurant's id
     * @return An objetc containing the restaurant's menu list of products
     **/
    @LogBean
    public List<Product> execute(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new AppException("Restaurant not found", HttpStatus.BAD_REQUEST));
        return restaurant.getProducts();
    }
}
