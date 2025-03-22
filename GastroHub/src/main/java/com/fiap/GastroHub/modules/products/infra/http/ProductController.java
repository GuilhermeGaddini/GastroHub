package com.fiap.GastroHub.modules.products.infra.http;

import com.fiap.GastroHub.modules.products.dtos.*;
import com.fiap.GastroHub.modules.products.usecases.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             GetAllProductsUseCase getAllProductsUseCase,
                             GetProductByIdUseCase getProductByIdUseCase,
                             DeleteProductUseCase deleteProductUseCase) {

        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }


    @Operation(summary = "Criar um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateUpdateProductRequest request
    ) {
        ProductResponse createdProduct = createProductUseCase.execute(request);
        return ResponseEntity.ok(createdProduct);
    }


    @Operation(summary = "Obter informações de todos os produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        logger.info("/products");
        List<ProductResponse> products = getAllProductsUseCase.execute();
        return ResponseEntity.ok(products);
    }


    @Operation(summary = "Obter informações de um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@RequestHeader("Authorization") String token, @PathVariable long id) {

        ProductResponse productResponse = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(productResponse);
    }


    @Operation(summary = "Atualizar informações de um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateUpdateProductRequest request
    ) {
        logger.info("PUT -> /products/{}", id);
        ProductResponse updatedProduct = updateProductUseCase.execute(id, request);
        return ResponseEntity.ok(updatedProduct);
    }


    @Operation(summary = "Deletar um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        logger.info("DELETE -> /products/{}", id);
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
