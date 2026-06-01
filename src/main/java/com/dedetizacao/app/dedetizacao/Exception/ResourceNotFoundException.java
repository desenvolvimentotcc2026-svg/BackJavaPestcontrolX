package com.dedetizacao.app.dedetizacao.Exception;

// Estende RuntimeException para que o java não nos obrigue a usar try-catch em todo lugar para executar e capturar o erro.//
public class ResourceNotFoundException extends RuntimeException {

    // Repassa a mensagem de erro (ex: "Cliente não encontrado") para a classe pai (Exception).
    public ResourceNotFoundException(String message) {
        super(message);
    }

}