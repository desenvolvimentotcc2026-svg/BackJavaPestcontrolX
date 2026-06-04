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

    public static final Map<Long, String> userTokensDatabase = new ConcurrentHashMap<>();

    public NotificationController(OrdemDeServicoRepository ordemRepository,
                                  SimpMessagingTemplate messagingTemplate,
                                  NotificationService notificationService) {
        this.ordemRepository = ordemRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @PostMapping("/trigger-acceptance")
    public ResponseEntity<?> dispararAceitacaoOrdemRealTime(@RequestParam Long clienteId, @RequestParam Long ordemId) {
        OrdemDeServico os = ordemRepository.findById(ordemId).orElse(null);
        if (os == null) {
            return ResponseEntity.status(404).body("Ordem de serviço não encontrada.");
        }

        os.setStatus("ACEITA");
        ordemRepository.save(os);

        // Notificação via WebSocket para atualização imediata na UI Android
        Map<String, Object> alertaGeral = new HashMap<>();
        alertaGeral.put("tipo", "OS_ACEITA");
        alertaGeral.put("ordemId", ordemId);
        alertaGeral.put("status", "ACEITA");
        alertaGeral.put("mensagem", "Sua solicitação de dedetização foi aceita! O chat está liberado.");

        String topicoCliente = "/topic/cliente/" + clienteId;
        messagingTemplate.convertAndSend(topicoCliente, alertaGeral);
        System.out.println("--> [WEBSOCKET] Alerta enviado para: " + topicoCliente);

        // Disparo opcional via Push Notification caso o dispositivo esteja em segundo plano
        String tokenDispositivo = userTokensDatabase.get(clienteId);
        if (tokenDispositivo != null) {
            try {
                notificationService.sendPushNotification(
                        tokenDispositivo,
                        "Atualização de Ordem #" + ordemId,
                        "Sua solicitação foi aceita! O chat está liberado."
                );
            } catch (Exception e) {
                System.err.println("⚠️ Falha ao transmitir Push FCM em background: " + e.getMessage());
            }
        }
        return ResponseEntity.ok().build();
    }
}