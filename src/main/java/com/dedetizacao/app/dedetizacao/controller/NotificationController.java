package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Uso de ConcurrentHashMap para garantir thread-safety com múltiplos acessos de dispositivos
    public static final Map<Long, String> userTokensDatabase = new ConcurrentHashMap<>();

    // 1. Endpoint para o APP Android salvar o token do usuário logado
    @PostMapping("/register-token")
    public ResponseEntity<Map<String, String>> registerToken(
            @RequestParam Long usuarioId,
            @RequestParam String fcmToken) {

        if (usuarioId == null || fcmToken == null || fcmToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Dados inválidos para registro de token."));
        }

        userTokensDatabase.put(usuarioId, fcmToken);
        System.out.println("🚀 Token registrado com sucesso para o Usuário ID [" + usuarioId + "]: " + fcmToken);

        // Retorna um JSON estruturado para o Retrofit ler sem estourar Exception
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Token registrado com sucesso no backend!"
        ));
    }

    // 2. Endpoint chamado quando o técnico aceita a ordem (Dispara o Push)
    @PostMapping("/trigger-acceptance")
    public ResponseEntity<Map<String, String>> triggerAcceptance(
            @RequestParam Long clienteId,
            @RequestParam Long ordemId) {

        if (clienteId == null || ordemId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Parâmetros clienteId e ordemId são obrigatórios."));
        }

        // Busca o token do cliente no mapa em memória
        String clienteToken = userTokensDatabase.get(clienteId);

        if (clienteToken == null || clienteToken.isEmpty()) {
            System.out.println("⚠️ Alerta: Cliente [" + clienteId + "] não possui um dispositivo registrado.");
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Cliente não possui um dispositivo móvel registrado."
            ));
        }

        try {
            // Dispara a notificação em tempo real via Firebase Admin SDK
            notificationService.sendPushNotification(
                    clienteToken,
                    "Sua ordem foi aceita! 🛠️",
                    "O técnico já está vinculado à sua solicitação de serviço Nº " + ordemId
            );

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Notificação de aceitação disparada com sucesso!"
            ));
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar push notification: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Falha interna ao processar o disparo do Push: " + e.getMessage()
            ));
        }
    }
}