package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Service.OrdemDeServicoService;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import com.dedetizacao.app.dedetizacao.Service.NotificationService;
import com.dedetizacao.app.dedetizacao.controller.NotificationController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ordens") // Escopo isolado do Kanban, GPS e Técnicos
@CrossOrigin(origins = "*")
public class OrdemDeServicoController {

    private final OrdemDeServicoService service;
    private final MensagemRepository mensagemRepository;
    private final OrdemDeServicoRepository ordemRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public OrdemDeServicoController(OrdemDeServicoService service, MensagemRepository mensagemRepository,OrdemDeServicoRepository ordemRepository) {
        this.service = service;
        this.mensagemRepository = mensagemRepository;
        this.ordemRepository = ordemRepository;
    }

    @PostMapping("/criar")
    public ResponseEntity<OrdemDeServico> criarOrdem(@RequestBody OrdemDeServico novaOrdem) {

        novaOrdem.setDataAbertura(LocalDateTime.now());
        novaOrdem.setStatus("ABERTA"); // Inicia na esteira operacional de triagem

        OrdemDeServico salva = ordemRepository.save(novaOrdem);
        return ResponseEntity.ok(salva);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<OrdemDeServico>> listarPorEmpresa(@PathVariable Long empresaId) {
        List<OrdemDeServico> filtradas = service.listar().stream()
                .filter(o -> o.getEmpresa() != null && o.getEmpresa().getId().equals(empresaId))
                .toList();
        return ResponseEntity.ok(filtradas);
    }

    @GetMapping
    public List<OrdemDeServico> listarOrdens() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemDeServico> buscarPorId(@PathVariable Long id) {
        OrdemDeServico ordem = service.listar().stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
        return ordem != null ? ResponseEntity.ok(ordem) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemDeServico> atualizarOrdem(@PathVariable Long id, @RequestBody OrdemDeServico ordem) {
        ordem.setId(id);
        OrdemDeServico ordemSalva = service.salvar(ordem);

        // 🛡️ INTEGRAÇÃO ABSURDA: Push Notification + Chat de Status Automatizado
        if (ordemSalva.getStatus() != null && (ordemSalva.getStatus().equalsIgnoreCase("ACEITA") || ordemSalva.getStatus().equalsIgnoreCase("EM_ANDAMENTO"))) {
            if (ordemSalva.getCliente() != null) {
                Long clienteId = ordemSalva.getCliente().getId();
                String tokenCelularCliente = NotificationController.userTokensDatabase.get(clienteId);

                try {
                    // 1. Dispara Push Notification via FCM
                    if (tokenCelularCliente != null && !tokenCelularCliente.isEmpty()) {
                        notificationService.sendPushNotification(
                                tokenCelularCliente,
                                "Técnico a Caminho! 🛠️",
                                "Sua Ordem de Serviço Nº " + ordemSalva.getId() + " mudou para: " + ordemSalva.getStatus()
                        );
                    }

                    // 2. Cria mensagem sistêmica dentro do Chat interno automaticamente
                    // Mensagem msgStatus = new Mensagem();
                    // msgStatus.setOrdemDeServico(ordemSalva);
                    // msgStatus.setConteudo("📢 [Sistema]: A ordem de serviço mudou de status para [" + ordemSalva.getStatus() + "]. O canal de atendimento está aberto.");
                    // msgStatus.setRemetente("SISTEMA");
                    // mensagemRepository.save(msgStatus);

                } catch (Exception e) {
                    System.err.println("⚠️ Falha em automações de notificação/chat: " + e.getMessage());
                }
            }
        }
        return ResponseEntity.ok(ordemSalva);
    }

    // 🛰️ TRANSMISSÃO TELEMÉTRICA DO GPS VIA WEBSOCKETS (FIM DO CTRL+Z!)
    @PutMapping("/{id}/gps")
    public ResponseEntity<String> atualizarGps(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        // Montagem higienizada do JSON que o mapa do painel web lê
        String payloadGps = String.format("{\"ordemId\": %d, \"latitude\": %f, \"longitude\": %f}", id, latitude, longitude);

        // Dispara em tempo real pelo Broker do Spring Boot no tópico específico daquela OS
        messagingTemplate.convertAndSend("/topic/gps/" + id, payloadGps);

        System.out.println("🛰️ Telemetria real-time OS #" + id + " injetada no WS: Lat " + latitude + " | Lng " + longitude);
        return ResponseEntity.ok("Coordenadas de geolocalização transmitidas.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}