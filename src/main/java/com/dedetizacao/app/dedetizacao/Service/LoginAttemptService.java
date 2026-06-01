package com.dedetizacao.app.dedetizacao.Service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTENTIVAS = 3;
    // Guarda os erros
    private Map<String, Integer> tentativasCache = new ConcurrentHashMap<>();
    // Guarda até quando o usuário está bloqueado
    private Map<String, LocalDateTime> bloqueioCache = new ConcurrentHashMap<>();

    public void loginFalhou(String emailOuTelefone) {
        int tentativas = tentativasCache.getOrDefault(emailOuTelefone, 0) + 1;
        tentativasCache.put(emailOuTelefone, tentativas);

        if (tentativas >= MAX_ATTENTIVAS) {
            // Bloqueia por 15 minutos (aumenta o tempo conforme necessário)
            bloqueioCache.put(emailOuTelefone, LocalDateTime.now().plusMinutes(15));
        }
    }

    public void loginSucesso(String emailOuTelefone) {
        tentativasCache.remove(emailOuTelefone);
        bloqueioCache.remove(emailOuTelefone);
    }

    public boolean estaBloqueado(String emailOuTelefone) {
        LocalDateTime tempoDesbloqueio = bloqueioCache.get(emailOuTelefone);
        if (tempoDesbloqueio == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(tempoDesbloqueio)) {
            // Tempo de bloqueio passou, libera o cara
            bloqueioCache.remove(emailOuTelefone);
            tentativasCache.remove(emailOuTelefone);
            return false;
        }
        return true;
    }
}