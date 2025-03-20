package com.fiap.GastroHub.modules.users.exceptions;

import com.fiap.GastroHub.shared.exception.GastroHubException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class UserException extends GastroHubException {
    public UserException(String message, HttpStatus status) {
        super(message, status);
    }
}