package com.fiap.GastroHub.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class GastroHubException extends RuntimeException{
    private String message;
    private HttpStatus statusCode;
}
