package cl.romboticket.ms_pagos.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        //LinkedHashMap para mantener el orden de inserción

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
        // Error 400 si se enviaron datos inválidos
    }

    // Atrapa los estados inválidos: "Ticket ya emitido", "Orden no pagada"
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(
            IllegalStateException ex) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", ex.getMessage());

        // 409 Conflict es el código HTTP correcto cuando hay un conflicto con el estado actual
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    //integridad de los datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", "Operación rechazada: No puedes eliminar este registro porque ya está siendo utilizado por otras partes del sistema.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /* Este es por si ocurre un error que no teníamos previsto
     * devuelve un Error 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", ex.getMessage());


        if (ex.getMessage().toLowerCase().contains("no encontrado") || ex.getMessage().toLowerCase().contains("no encontrada")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.badRequest().body(error);
    }
}