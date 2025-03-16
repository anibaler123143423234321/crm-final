package com.midas.crm.utils.errorHandler;

import com.midas.crm.exceptions.MidasExceptions;
import com.midas.crm.utils.GenericResponse;
import com.midas.crm.utils.GenericResponseConstants;
import com.midas.crm.utils.MidasErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse<Object> handleGeneralException(Exception ex) {
        return new GenericResponse<>(
                GenericResponseConstants.ERROR,
                GenericResponseConstants.OPERATION_FAILED,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MidasExceptions.class)
    public ResponseEntity<GenericResponse<Object>> handleGatherlyException(MidasExceptions ex) {
        MidasErrorMessage errorMessage = ex.getErrorMessage();
        HttpStatus status = mapToHttpStatus(errorMessage);

        return new ResponseEntity<>(
                new GenericResponse<>(
                        GenericResponseConstants.ERROR,
                        errorMessage.getErrorMessage(),
                        null
                ),
                status
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new GenericResponse<>(
                GenericResponseConstants.ERROR,
                "Validation failed",
                errors
        );
    }

    private HttpStatus mapToHttpStatus(MidasErrorMessage errorMessage) {
        return switch (errorMessage) {
            case USUARIO_ALREADY_EXISTS, CLIENTERESIDENCIAL_ALREADY_EXISTS, COORDINADOR_ALREADY_EXISTS,
                 ASESOR_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case USUARIO_NOT_FOUND, CLIENTERESIDENCIAL_NOT_FOUND, COORDINADOR_NOT_FOUND,
                 ASESOR_NOT_FOUND, ASESOR_NOT_AVAILABLE, COORDINADOR_NOT_AVAILABLE -> HttpStatus.NOT_FOUND;
            case USUARIO_INVALID_LOGIN -> HttpStatus.UNAUTHORIZED;
            case CLIENTERESIDENCIAL_INVALID_DATA, ASIGNAR_COORDINADOR_FAILED,
                 ASIGNACION_ASESOR_FAILED -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}