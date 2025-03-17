package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAllProductsUseCase {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    /**
     * Executes the get all products use case
     *
     * @return A list containing information on all products
     **/
    @LogBean
    public List<ProductResponse> execute() {
        try {
            return productRepository.findAll().stream()
                    .map(product -> modelMapper.map(product, ProductResponse.class))
                    .collect(Collectors.toList());
        } catch (Error e) {
            throw new AppException("Error fetching products", HttpStatus.BAD_REQUEST);
        }
    }
}
