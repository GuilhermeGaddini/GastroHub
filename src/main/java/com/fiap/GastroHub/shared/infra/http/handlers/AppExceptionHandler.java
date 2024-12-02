package com.fiap.GastroHub.shared.infra.http.handlers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fiap.GastroHub.shared.AppException;
import com.fiap.GastroHub.shared.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    @Getter
    @AllArgsConstructor
    private static class JsonResponse {
        public String errorMessage;
        public int errorCode;
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatusCode().value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request){
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String errorMessage = "Malformed JSON request";
        if (!(mostSpecificCause instanceof InvalidFormatException invalidFormatException)){
            return new ResponseEntity<>(new JsonResponse(errorMessage, 400), HttpStatus.BAD_REQUEST);
        }

        if (!invalidFormatException.getTargetType().isEnum()){
            return new ResponseEntity<>(new JsonResponse(errorMessage, 400), HttpStatus.BAD_REQUEST);
        }

        String fieldName = invalidFormatException.getPath().isEmpty() ? "Unknown" : invalidFormatException.getPath().getLast().getFieldName();
        errorMessage = String.format("Invalid value for the '%s' field", fieldName);

        return new ResponseEntity<>(new JsonResponse(errorMessage, 400), HttpStatus.BAD_REQUEST);
    }
}
