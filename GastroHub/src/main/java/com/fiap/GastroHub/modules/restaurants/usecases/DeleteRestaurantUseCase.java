package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.exceptions.RestaurantException;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteRestaurantUseCase {
    private final RestaurantRepository restaurantRepository;

    /**
     * Executes the restaurant deletion use case
     *
     * @param id Restaurant's id
     **/
    @LogBean
    public void execute(Long id) {
        try {
            Restaurant restaurant = restaurantRepository.findById(id)
                    .orElseThrow(() -> new RestaurantException("Restaurant not found", HttpStatus.NOT_FOUND));
            restaurantRepository.delete(restaurant);
        } catch (RuntimeException e) {
            throw new RestaurantException("Unexpected error while deleting the restaurant", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
