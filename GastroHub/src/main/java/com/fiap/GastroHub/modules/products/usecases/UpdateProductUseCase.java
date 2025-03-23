package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCase {
    private static final Logger logger = LogManager.getLogger(UpdateProductUseCase.class);
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public UpdateProductUseCase(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Executes the update product use case
     *
     * @param id Product's id
     * @param request The object with the product's information to be updated
     * @return An object confirming the product's changed information
     **/
    @LogBean
    public ProductResponse execute(Long id, CreateUpdateProductRequest request) {
        logger.info("Trying to update a product with the following id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found", HttpStatus.NOT_FOUND));

        modelMapper.map(request, product);
        product = productRepository.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }
}
