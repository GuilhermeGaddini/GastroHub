package com.fiap.GastroHub.modules.products.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.usecases.CreateProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
//@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {
//    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CreateProductUseCase createProductUseCase;

    @InjectMocks
    private ProductController productController;

//    public ProductControllerTest(MockMvc mockMvc) {
//        this.mockMvc = mockMvc;
//    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("Should create a product successfully")
    public void testCreateProductSuccess() {
        CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                "Produto 1",
                "Descrição do produto",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg",
                1L
        );

        ProductResponse createProductResponse = new ProductResponse(
                "Produto 1",
                BigDecimal.valueOf(100.00),
                "Disponível",
                "/img/path.jpeg"
        );

        when(createProductUseCase.execute(createProductRequest)).thenReturn(createProductResponse);

        // Execução do teste
        ResponseEntity<ProductResponse> response = productController.createProduct(createProductRequest);

        // Verificações
        assertEquals(ResponseEntity.ok(createProductResponse), response);
        verify(createProductUseCase).execute(createProductRequest);
    }
}
