package com.fiap.GastroHub.modules.roles.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class RoleException extends RuntimeException {
    private String message;
    private HttpStatus statusCode;
}