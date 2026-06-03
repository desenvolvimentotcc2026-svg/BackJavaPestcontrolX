package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Repository.OrdemDeServicoRepository;
import com.dedetizacao.app.dedetizacao.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final OrdemDeServicoRepository ordemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    // Mantido como public static para manter compatibilidade com outras classes que o acessam
    public static final Map<Long, String> userTokensDatabase = new ConcurrentHashMap<>();

    // Injeção de dependência via construtor (mais limpo e seguro)
    public NotificationController(OrdemDeServicoRepository ordemRepository,
                                  SimpMessagingTemplate messagingTemplate,
                                  NotificationService notificationService) {
        this.ordemRepository = ordemRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(@RequestParam Long usuarioId, @RequestParam String fcmToken) {
        if (usuarioId == null || fcmToken == null || fcmToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Parâmetros inválidos.");
        }

        userTokensDatabase.put(usuarioId, fcmToken);
        System.out.println("--> Token registrado para usuário: " + usuarioId);
        return ResponseEntity.ok("Token registrado com sucesso.");
    }

    @PostMapping("/trigger-acceptance")
    public ResponseEntity<?> aceitarOrdem(@RequestParam Long clienteId, @RequestParam Long ordemId) {
        // 1. Atualiza o status no Banco
        OrdemDeServico os = ordemRepository.findById(ordemId).orElse(null);
        if (os == null) {
            return ResponseEntity.status(404).body("Ordem de serviço não encontrada.");
        }

        os.setStatus("ACEITA");
        ordemRepository.save(os);

        // 2. Notificação via WebSocket (Tempo Real)
        Map<String, Object> alertaGeral = new HashMap<>();
        alertaGeral.put("tipo", "OS_ACEITA");
        alertaGeral.put("ordemId", ordemId);
        alertaGeral.put("status", "ACEITA");
        alertaGeral.put("mensagem", "Sua solicitação de dedetização foi aceita! O chat está liberado.");

        String topicoCliente = "/topic/cliente/" + clienteId;
        messagingTemplate.convertAndSend(topicoCliente, alertaGeral);
        System.out.println("--> [WEBSOCKET] Alerta enviado para: " + topicoCliente);

        // 3. Notificação Push via Firebase (Background/Notificação Nativa)
        String tokenDispositivo = userTokensDatabase.get(clienteId);
        if (tokenDispositivo != null) {
            notificationService.sendPushNotification(
                    tokenDispositivo,
                    "Atualização de Ordem #" + ordemId,
                    "Sua solicitação foi aceita! O chat está liberado."
            );
        } else {
            System.out.println("--> [FCM PUSH] Nenhum token mobile ativo para o cliente " + clienteId);
        }

        return ResponseEntity.ok("Notificação disparada com sucesso.");
    }
}