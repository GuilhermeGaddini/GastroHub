package com.fiap.GastroHub.modules.restaurants.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantException extends RuntimeException {
    private String message;
    private HttpStatus statusCode;

}
