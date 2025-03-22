package com.fiap.GastroHub.modules.products.usecases;

import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.products.infra.orm.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Product Use Case Test Class")
public class DeleteProductUseCaseTest {
    @Mock
    ProductRepository productRepository;

    AutoCloseable mock;

    private DeleteProductUseCase deleteProductUseCase;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        deleteProductUseCase = new DeleteProductUseCase(productRepository);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void execute_ValidId_DeleteProduct() {
        Product product = Product.builder()
                .id(1L)
                .name("Produto 1")
                .availability("Disponível")
                .price(BigDecimal.valueOf(100.00))
                .description("Descrição do produto")
                .picPath("/img/imgPath.png")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        deleteProductUseCase.execute(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Should throw a invalid id exception")
    void execute_InvalidId_ThrowsProductException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductException exception = assertThrows(ProductException.class, () -> deleteProductUseCase.execute(1L));

        assertEquals("Product with ID " + 1L + " not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw a nonexistent id exception")
    void execute_NullId_ThrowsProductException() {
        ProductException exception = assertThrows(ProductException.class, () -> deleteProductUseCase.execute(null));

        assertEquals("Product with ID null not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).delete(any());
    }
}
