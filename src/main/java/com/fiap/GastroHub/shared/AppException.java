package com.fiap.GastroHub.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class AppException extends RuntimeException {
    private String message;
    private HttpStatus statusCode;

}
