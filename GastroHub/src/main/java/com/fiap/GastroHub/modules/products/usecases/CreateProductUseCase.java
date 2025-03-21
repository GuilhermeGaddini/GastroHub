package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.modules.restaurants.infra.orm.entities.Restaurant;
import com.fiap.GastroHub.modules.restaurants.infra.orm.repositories.RestaurantRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreateProductUseCase {
    private static final Logger logger = LogManager.getLogger(CreateProductUseCase.class);
    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the product creation use case
     *
     * @param request Object containing the product info
     * @return Response object with product created successfully
     **/
    @LogBean
    @Transactional
    public ProductResponse execute(CreateUpdateProductRequest request) {
        logger.info("Trying to create a new product with the following info: {}", request.getName());

        try {
            Product product = modelMapper.map(request, Product.class);
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurant()).get();
            product.setRestaurant(restaurant);
            productRepository.save(product);

            logger.info("New product created successfully");
            return modelMapper.map(product, ProductResponse.class);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new ProductException("An unexpected error occurred while creating the product.", HttpStatus.BAD_REQUEST);
        }
    }
}
