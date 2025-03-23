package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAllRestaurantsUseCase {
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get all restaurants use case
     *
     * @return A list containing information on all restaurants
     **/
    @LogBean
    public List<RestaurantResponse> execute() {
        try {
            return restaurantRepository.findAll().stream()
                    .map(restaurant -> modelMapper.map(restaurant, RestaurantResponse.class))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new RestaurantException("Error fetching restaurants", HttpStatus.BAD_REQUEST);
        }
    }
}
