package com.fiap.GastroHub.modules.products.exceptions;

import com.fiap.GastroHub.shared.exception.GastroHubException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class ProductException extends GastroHubException {
    public ProductException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }
}