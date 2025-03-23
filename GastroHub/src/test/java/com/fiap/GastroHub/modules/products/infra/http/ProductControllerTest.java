package com.fiap.GastroHub.modules.products.infra.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.GastroHub.modules.products.dtos.CreateUpdateProductRequest;
import com.fiap.GastroHub.modules.products.dtos.ProductResponse;
import com.fiap.GastroHub.modules.products.exceptions.ProductException;
import com.fiap.GastroHub.modules.products.usecases.*;
import com.fiap.GastroHub.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Product Controller test Class")
public class ProductControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private GetAllProductsUseCase getAllProductsUseCase;

    @Mock
    private GetProductByIdUseCase getProductByIdUseCase;

    @Mock
    private DeleteProductUseCase deleteProductUseCase;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        ProductController productController = new ProductController(createProductUseCase, updateProductUseCase, getAllProductsUseCase, getProductByIdUseCase, deleteProductUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Nested
    @DisplayName("Create product cases")
    class CreateProduct {
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

            ResponseEntity<ProductResponse> response = productController.createProduct(createProductRequest);

            assertEquals(ResponseEntity.ok(createProductResponse), response);
            verify(createProductUseCase).execute(createProductRequest);
        }

        @Test
        @DisplayName("Should not create a product and throw name exception")
        public void testCreateProduct_exception_blankName() throws Exception {
            CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                    "",
                    "Descrição do produto",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg",
                    1L
            );

            mockMvc.perform(post("/products/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createProductRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Name is required"));
        }

        @Test
        @DisplayName("Should not create a product and throw price exception")
        public void testCreateProduct_exception_nullPrice() throws Exception {
            CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                    "Produto 1",
                    "Descrição do produto",
                    null,
                    "Disponível",
                    "/img/path.jpeg",
                    1L
            );

            mockMvc.perform(post("/products/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createProductRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Price is required"));
        }

        @Test
        @DisplayName("Should not create a product and throw restaurant exception")
        public void testCreateProduct_exception_nullRestaurant() throws Exception {
            CreateUpdateProductRequest createProductRequest = new CreateUpdateProductRequest(
                    "Produto 1",
                    "Descrição do produto",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg",
                    null
            );

            mockMvc.perform(post("/products/create").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(createProductRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors[0]").value("Restaurant ID is required"));
        }
    }

    @Nested
    @DisplayName("Update products cases")
    class UpdateProducts {
        @Test
        @DisplayName("Should update a product successfully")
        void updateProduct_success() throws Exception {
            CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                    "Produto 1",
                    "Descrição do produto",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg",
                    1L
            );

            ProductResponse updateProductResponse = new ProductResponse(
                    "Produto 1",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg"
            );

            when(updateProductUseCase.execute(eq(1L), any(CreateUpdateProductRequest.class))).thenReturn(updateProductResponse);

            mockMvc.perform(put("/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateProductRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(updateProductRequest.getName()))
                    .andDo(print());

            verify(updateProductUseCase, times(1)).execute(eq(1L), any(CreateUpdateProductRequest.class));
        }

        @Test
        @DisplayName("Should not update a product and throw name exception")
        public void testUpdateProduct_exception_blankName() throws Exception {
            CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                    "",
                    "Descrição do produto",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg",
                    1L
            );

            mockMvc.perform(put("/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateProductRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Name is required"))
                    .andDo(print());
        }

        @Test
        @DisplayName("Should not update a product and throw price exception")
        public void testUpdateProduct_exception_nullPrice() throws Exception {
            CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                    "Produto 1",
                    "Descrição do produto",
                    null,
                    "Disponível",
                    "/img/path.jpeg",
                    1L
            );

            mockMvc.perform(put("/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateProductRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Price is required"))
                    .andDo(print());
        }

        @Test
        @DisplayName("Should not update a product and throw restaurant exception")
        public void testUpdateProduct_exception_nullRestaurant() throws Exception {
            CreateUpdateProductRequest updateProductRequest = new CreateUpdateProductRequest(
                    "Produto 1",
                    "Descrição do produto",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg",
                    null
            );

            mockMvc.perform(put("/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateProductRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]").value("Restaurant ID is required"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Get products cases")
    class GetProducts {
        @Test
        @DisplayName("Should get all products successfully")
        void getAllProducts_success() throws Exception {
            ProductResponse updateProductResponse1 = new ProductResponse(
                    "Produto 1",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg"
            );

            ProductResponse updateProductResponse2 = new ProductResponse(
                    "Produto 2",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg"
            );

            when(getAllProductsUseCase.execute()).thenReturn(Arrays.asList(updateProductResponse1, updateProductResponse2));

            mockMvc.perform(get("/products")
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andDo(print());

            verify(getAllProductsUseCase, times(1)).execute();
        }

        @Test
        @DisplayName("Should get a product's information by its given id")
        void getProductById_success() throws Exception {
            ProductResponse updateProductResponse = new ProductResponse(
                    "Produto 1",
                    BigDecimal.valueOf(100.00),
                    "Disponível",
                    "/img/path.jpeg"
            );

            when(getProductByIdUseCase.execute(eq(1L))).thenReturn(updateProductResponse);

            mockMvc.perform(get("/products/{id}", 1)
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(updateProductResponse.getName()))
                    .andDo(print());

            verify(getProductByIdUseCase, times(1)).execute(1L);
        }
    }

    @Nested
    @DisplayName("Delete products cases")
    class DeleteProducts {
        @Test
        @DisplayName("Should delete a product successfully")
        void deleteProduct_success() throws Exception {
            doNothing().when(deleteProductUseCase).execute(1L);

            mockMvc.perform(delete("/products/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            verify(deleteProductUseCase, times(1)).execute(1L);
        }

        @Test
        @DisplayName("Should not be able to delete product")
        void deleteProduct_productNotFound() throws Exception {
            doThrow(new ProductException("Product not found", HttpStatus.NOT_FOUND))
                    .when(deleteProductUseCase).execute(999L);

            mockMvc.perform(delete("/products/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found"))
                    .andDo(print());

            verify(deleteProductUseCase, times(1)).execute(999L);
        }
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
