package com.fiap.GastroHub.modules.roles.exceptions;

import com.fiap.GastroHub.shared.exception.GastroHubException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class RoleException extends GastroHubException {
    public RoleException(String message, HttpStatus status) {
        super(message, status);
    }
}