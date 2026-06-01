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

    // Guarda temporariamente em memória os tokens FCM associados aos IDs dos usuários
    // Substitua pela lógica do seu UsuarioRepository/Banco se preferir salvar em tabela
    private static final Map<Long, String> fcmTokensCache = new ConcurrentHashMap<>();

    public NotificationController(OrdemDeServicoRepository ordemRepository, SimpMessagingTemplate messagingTemplate) {
        this.ordemRepository = ordemRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // 1. Mapeamento exato da rota do aplicativo para registrar o token de notificação
    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(@RequestParam Long usuarioId, @RequestParam String fcmToken) {
        if (usuarioId == null || fcmToken == null || fcmToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Parâmetros inválidos.");
        }

        fcmTokensCache.put(usuarioId, fcmToken);
        System.out.println("--> [FCM] Token registrado para o usuário [" + usuarioId + "]: " + fcmToken);

        Map<String, String> response = new HashMap<>();
        response.put("status", "TOKEN_REGISTRADO_SUCESSO");
        return ResponseEntity.ok(response);
    }

    // 2. Mapeamento exato da rota que o seu painel WEB vai chamar ao clicar em "ACEITAR"
    @PostMapping("/trigger-acceptance")
    public ResponseEntity<?> triggerAcceptance(@RequestParam Long clienteId, @RequestParam Long ordemId) {
        System.out.println("--> [FLUXO] Solicitando aceitação. Cliente: " + clienteId + " | OS: " + ordemId);

        // 1. Busca a Ordem de Serviço no banco e atualiza o status
        OrdemDeServico os = ordemRepository.findById(ordemId).orElse(null);
        if (os == null) {
            return ResponseEntity.status(404).body("Ordem de serviço não encontrada.");
        }

        // Modifica o status para que o cliente saiba que foi aceito
        os.setStatus("ACEITA"); // Ou "EM_ANDAMENTO", conforme seu padrão
        ordemRepository.save(os);

        // 2. DISPARO VIA WEBSOCKET (Atualização Instantânea de Tela no App)
        // O aplicativo do cliente estará escutando esse tópico dinâmico
        Map<String, Object> alertaGeral = new HashMap<>();
        alertaGeral.addProperty("tipo", "OS_ACEITA");
        alertaGeral.addProperty("ordemId", ordemId);
        alertaGeral.addProperty("status", "ACEITA");
        alertaGeral.addProperty("mensagem", "Sua solicitação de dedetização foi aceita! O chat está liberado.");

        String topicoCliente = "/topic/cliente/" + clienteId;
        messagingTemplate.convertAndSend(topicoCliente, alertaGeral);
        System.out.println("--> [WEBSOCKET] Alerta de OS Aceita enviado para: " + topicoCliente);

        // 3. LOG DO PUSH NOTIFICATION (Firebase FCM)
        String tokenDispositivo = fcmTokensCache.get(clienteId);
        if (tokenDispositivo != null) {
            System.out.println("--> [FCM PUSH] Enviando notificação nativa para o token: " + tokenDispositivo);
            // Aqui entra o código do Firebase SDK se você for subir a notificação push de background.
            // Com o WebSocket ativo acima, se o app estiver aberto, ele já atualiza na hora!
        } else {
            System.out.println("--> [FCM PUSH] Nenhum token mobile ativo em background para este cliente no momento.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "OS_ACEITA_E_NOTIFICADA");
        return ResponseEntity.ok(response);
    }
}