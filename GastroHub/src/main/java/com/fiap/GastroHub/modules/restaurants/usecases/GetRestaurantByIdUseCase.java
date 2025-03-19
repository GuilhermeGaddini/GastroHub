package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRestaurantByIdUseCase {
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get restaurant use case
     *
     * @param id Restaurant's id
     * @return An objetc containing the restaurant's information
     **/
    @LogBean
    public RestaurantResponse execute(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException("Restaurant not found", HttpStatus.BAD_REQUEST));
        return modelMapper.map(restaurant, RestaurantResponse.class);
    }

}
