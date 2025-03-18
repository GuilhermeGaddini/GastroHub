package com.fiap.GastroHub.modules.restaurants.infra.http;

import com.fiap.GastroHub.modules.products.infra.orm.entities.Product;
import com.fiap.GastroHub.modules.restaurants.dtos.*;
import com.fiap.GastroHub.modules.restaurants.usecases.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("restaurants")
public class RestaurantController {
    private static final Logger logger = LogManager.getLogger(RestaurantController.class);

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final GetAllRestaurantsUseCase getAllRestaurantsUseCase;
    private final GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    private final GetRestaurantMenuUseCase getRestaurantMenuUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                UpdateRestaurantUseCase updateRestaurantUseCase,
                                GetAllRestaurantsUseCase getAllRestaurantsUseCase,
                                GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                GetRestaurantMenuUseCase getRestaurantMenuUseCase,
                                DeleteRestaurantUseCase deleteRestaurantUseCase) {

        this.createRestaurantUseCase = createRestaurantUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.getAllRestaurantsUseCase = getAllRestaurantsUseCase;
        this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
        this.getRestaurantMenuUseCase = getRestaurantMenuUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
    }

    @Operation(summary = "Criar um restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PostMapping("/create")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @RequestBody CreateUpdateRestaurantRequest request
    ) {
        RestaurantResponse createdRestaurant = createRestaurantUseCase.execute(request);
        return ResponseEntity.ok(createdRestaurant);
    }

    @Operation(summary = "Obter informações de todos os restaurantes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        logger.info("/restaurants");
        List<RestaurantResponse> restaurants = getAllRestaurantsUseCase.execute();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "Obter informações de um restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById( @PathVariable long id) {

        RestaurantResponse restaurantResponse = getRestaurantByIdUseCase.execute(id);
        return ResponseEntity.ok(restaurantResponse);
    }

    @Operation(summary = "Obter menu (lista de produtos) de um restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @GetMapping("/{id}/menu")
    public ResponseEntity<List<Product>> getRestaurantMenuById(@PathVariable long id) {

        List<Product> products = getRestaurantMenuUseCase.execute(id);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

    @Operation(summary = "Atualizar informações de um restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable("id") Long id,
            @RequestBody CreateUpdateRestaurantRequest request
    ) {
        logger.info("PUT -> /restaurants/{}", id);
        RestaurantResponse updatedRestaurant = updateRestaurantUseCase.execute(id, request);
        return ResponseEntity.ok(updatedRestaurant);
    }


    @Operation(summary = "Deletar um restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro Interno")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Long id) {
        logger.info("DELETE -> /restaurants/{}", id);
        deleteRestaurantUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
