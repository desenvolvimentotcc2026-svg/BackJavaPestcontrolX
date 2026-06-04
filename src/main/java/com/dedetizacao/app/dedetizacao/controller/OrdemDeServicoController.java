package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Service.OrdemDeServicoService;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import com.dedetizacao.app.dedetizacao.Repository.OrdemDeServicoRepository;
import com.dedetizacao.app.dedetizacao.Service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ordens")
@CrossOrigin(origins = "*")
public class OrdemDeServicoController {

    private final OrdemDeServicoService service;
    private final MensagemRepository mensagemRepository;
    private final OrdemDeServicoRepository ordemRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public OrdemDeServicoController(OrdemDeServicoService service,
                                    MensagemRepository mensagemRepository,
                                    OrdemDeServicoRepository ordemRepository) {
        this.service = service;
        this.mensagemRepository = mensagemRepository;
        this.ordemRepository = ordemRepository;
    }

    @PostMapping("/criar")
    public ResponseEntity<OrdemDeServico> criarOrdem(@RequestBody OrdemDeServico ordem) {
        if (ordem.getDataAbertura() == null) {
            ordem.setDataAbertura(LocalDateTime.now());
        }
        OrdemDeServico ordemSalva = service.salvar(ordem);
        return ResponseEntity.ok(ordemSalva);
    }

    @GetMapping
    public ResponseEntity<List<OrdemDeServico>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemDeServico> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🛰️ TRANSMISSÃO TELEMÉTRICA DO GPS VIA WEBSOCKETS
    @PutMapping("/{id}/gps")
    public ResponseEntity<String> atualizarGps(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        String payloadGps = String.format("{\\\"ordemId\\\": %d, \\\"latitude\\\": %f, \\\"longitude\\\": %f}", id, latitude, longitude);
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