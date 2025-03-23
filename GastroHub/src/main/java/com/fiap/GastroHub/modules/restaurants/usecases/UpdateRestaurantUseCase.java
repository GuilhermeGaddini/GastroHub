package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UpdateRestaurantUseCase {
    private static final Logger logger = LogManager.getLogger(UpdateRestaurantUseCase.class);
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    public UpdateRestaurantUseCase(RestaurantRepository restaurantRepository, ModelMapper modelMapper) {
        this.restaurantRepository = restaurantRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Executes the update restaurant use case
     *
     * @param id Restaurant's id
     * @param request The object with the restaurant's information to be updated
     * @return An object confirming the restaurant's changed information
     **/
    @LogBean
    public RestaurantResponse execute(Long id, CreateUpdateRestaurantRequest request) {
        logger.info("Trying to update a restaurant with the following id: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException("Restaurant not found", HttpStatus.NOT_FOUND));

        modelMapper.map(request, restaurant);
        restaurant = restaurantRepository.save(restaurant);
        return modelMapper.map(restaurant, RestaurantResponse.class);
    }
}
