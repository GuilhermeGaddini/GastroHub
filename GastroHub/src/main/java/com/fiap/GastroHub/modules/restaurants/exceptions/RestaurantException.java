package com.fiap.GastroHub.modules.restaurants.exceptions;

import com.fiap.GastroHub.shared.exception.GastroHubException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class RestaurantException extends GastroHubException {
    public RestaurantException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }

}
