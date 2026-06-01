package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Repository.OrdemDeServicoRepository;
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

    // CORREÇÃO 1: Alterado para public e renomeado para 'userTokensDatabase' para o OrdemDeServicoController conseguir acessar
    public static final Map<Long, String> userTokensDatabase = new ConcurrentHashMap<>();

    public NotificationController(OrdemDeServicoRepository ordemRepository, SimpMessagingTemplate messagingTemplate) {
        this.ordemRepository = ordemRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(@RequestParam Long usuarioId, @RequestParam String fcmToken) {
        if (usuarioId == null || fcmToken == null || fcmToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Parâmetros inválidos.");
        }

        // Atualizado para usar o nome correto da variável
        userTokensDatabase.put(usuarioId, fcmToken);
        System.out.println("--> [FCM] Token registrado para o usuário [" + usuarioId + "]: " + fcmToken);

        Map<String, String> response = new HashMap<>();
        response.put("status", "TOKEN_REGISTRADO_SUCESSO");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trigger-acceptance")
    public ResponseEntity<?> triggerAcceptance(@RequestParam Long clienteId, @RequestParam Long ordemId) {
        System.out.println("--> [FLUXO] Solicitando aceitação. Cliente: " + clienteId + " | OS: " + ordemId);

        OrdemDeServico os = ordemRepository.findById(ordemId).orElse(null);
        if (os == null) {
            return ResponseEntity.status(404).body("Ordem de serviço não encontrada.");
        }

        os.setStatus("ACEITA");
        ordemRepository.save(os);

        // CORREÇÃO 2: Trocado '.addProperty()' por '.put()' que é o correto para Java Map
        Map<String, Object> alertaGeral = new HashMap<>();
        alertaGeral.put("tipo", "OS_ACEITA");
        alertaGeral.put("ordemId", ordemId);
        alertaGeral.put("status", "ACEITA");
        alertaGeral.put("mensagem", "Sua solicitação de dedetização foi aceita! O chat está liberado.");

        String topicoCliente = "/topic/cliente/" + clienteId;
        messagingTemplate.convertAndSend(topicoCliente, alertaGeral);
        System.out.println("--> [WEBSOCKET] Alerta de OS Aceita enviado para: " + topicoCliente);

        // Atualizado para usar o nome correto da variável
        String tokenDispositivo = userTokensDatabase.get(clienteId);
        if (tokenDispositivo != null) {
            System.out.println("--> [FCM PUSH] Enviando notificação nativa para o token: " + tokenDispositivo);
        } else {
            System.out.println("--> [FCM PUSH] Nenhum token mobile ativo em background para este cliente no momento.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "OS_ACEITA_E_NOTIFICADA");
        return ResponseEntity.ok(response);
    }
}