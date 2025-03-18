package com.fiap.GastroHub.modules.restaurants.usecases;

import com.fiap.GastroHub.modules.restaurants.dtos.CreateUpdateRestaurantRequest;
import com.fiap.GastroHub.modules.restaurants.dtos.RestaurantResponse;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.modules.roles.infra.orm.repositories.RoleRepository;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;
import com.fiap.GastroHub.modules.users.infra.orm.repositories.UserRepository;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateRestaurantUseCase {
    private static final Logger logger = LogManager.getLogger(CreateRestaurantUseCase.class);
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the restaurant creation use case
     *
     * @param request Object containing the restaurant info
     * @return Response object with restaurant created successfully
     **/
    @LogBean
    @Transactional
    public RestaurantResponse execute(CreateUpdateRestaurantRequest request) {
        logger.info("Trying to create a new restaurant with the following info: {}", request.getName());

        try {
            Restaurant restaurant = modelMapper.map(request, Restaurant.class);

            User user = userRepository.findById(request.getOwner())
                    .orElseThrow(() -> new AppException("Owner User not found", HttpStatus.NOT_FOUND));

            restaurant.setOwner(user);

            restaurantRepository.save(restaurant);

            logger.info("New restaurant created successfully");
            return modelMapper.map(restaurant, RestaurantResponse.class);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new AppException("An unexpected error occurred while creating the restaurant.", HttpStatus.BAD_REQUEST);
        }
    }
}
