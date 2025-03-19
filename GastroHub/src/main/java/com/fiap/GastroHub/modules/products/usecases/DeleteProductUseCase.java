package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import com.fiap.GastroHub.shared.infra.beans.LogBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {
    private final ProductRepository productRepository;

    /**
     * Executes the product deletion use case
     *
     * @param id Product's id
     **/
    @LogBean
    public void execute(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product with ID " + id + " not found", HttpStatus.BAD_REQUEST));
        productRepository.delete(product);
    }
}
