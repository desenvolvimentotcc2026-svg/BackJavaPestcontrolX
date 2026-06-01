
package com.dedetizacao.app.dedetizacao.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Centraliza o tratamento de erros de todos os Controllers em um só lugar.
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // Escuta especificamente quando um recurso não é encontrado.
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex){
        // Retorna o erro 404 (Not Found) de forma limpa, apenas com a mensagem personalizada que chama essa função lá nos services.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}



