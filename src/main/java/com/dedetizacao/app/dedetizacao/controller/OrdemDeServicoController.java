package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Service.OrdemDeServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ordens")
@CrossOrigin(origins = "*")
public class OrdemDeServicoController {

    private final OrdemDeServicoService service;
    private final SimpMessagingTemplate messagingTemplate;

    public OrdemDeServicoController(OrdemDeServicoService service, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping({"", "/criar"})
    public ResponseEntity<OrdemDeServico> criarOrdem(@RequestBody OrdemDeServico ordem) {
        OrdemDeServico ordemSalva = service.salvar(ordem);
        publicarMudancaStatus(ordemSalva);
        return ResponseEntity.ok(ordemSalva);
    }

    @GetMapping
    public ResponseEntity<List<OrdemDeServico>> listarTodas() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemDeServico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<OrdemDeServico>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @GetMapping("/cliente/{clienteId}/ativas")
    public ResponseEntity<List<OrdemDeServico>> listarAtivasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarAtivasPorCliente(clienteId));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<OrdemDeServico>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/ativas")
    public ResponseEntity<List<OrdemDeServico>> listarAtivasPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.listarAtivasPorEmpresa(empresaId));
    }

    // Compatibilidade com id de funcionário genérico
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<OrdemDeServico>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    // ALINHAMENTO MOBILE: Rota duplicada para mapear a chamada 'tecnico' do app Android
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<List<OrdemDeServico>> listarPorTecnico(@PathVariable Long tecnicoId) {
        return ResponseEntity.ok(service.listarPorFuncionario(tecnicoId));
    }

    // ALINHAMENTO MOBILE: Busca ordem ativa de um técnico específico
    @GetMapping("/tecnico/{tecnicoId}/ativa")
    public ResponseEntity<OrdemDeServico> buscarOrdemAtivaTecnico(@PathVariable Long tecnicoId) {
        return ResponseEntity.ok(service.buscarOrdemAtivaTecnico(tecnicoId));
    }

    // ALINHAMENTO MOBILE: Filtra a agenda da empresa por data diretamente na API
    @GetMapping("/empresa/{empresaId}/agenda")
    public ResponseEntity<List<OrdemDeServico>> buscarOrdensPorDataEEmpresa(
            @PathVariable Long empresaId,
            @RequestParam("data") String data) {
        return ResponseEntity.ok(service.buscarOrdensPorDataEEmpresa(data, empresaId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrdemDeServico>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<OrdemDeServico> aceitarOrdem(
            @PathVariable Long id,
            @RequestParam(value = "funcionarioId", required = false) Long funcionarioId) {
        OrdemDeServico updated = service.aceitar(id, funcionarioId);
        publicarMudancaStatus(updated);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<OrdemDeServico> iniciarOrdem(@PathVariable Long id) {
        OrdemDeServico updated = service.iniciar(id);
        publicarMudancaStatus(updated);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<OrdemDeServico> finalizarOrdem(
            @PathVariable Long id,
            @RequestBody(required = false) OrdemDeServico dadosFinalizacao) {
        OrdemDeServico updated = service.finalizar(id, dadosFinalizacao);
        publicarMudancaStatus(updated);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/gps")
    public ResponseEntity<OrdemDeServico> atualizarGps(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        OrdemDeServico updated = service.atualizarGps(id, latitude, longitude);
        String payloadGps = String.format("{\"ordemId\":%d,\"latitude\":%f,\"longitude\":%f}", id, latitude, longitude);
        messagingTemplate.convertAndSend("/topic/gps/" + id, payloadGps);
        publicarMudancaStatus(updated);
        return ResponseEntity.ok(updated);
    }

    // ALINHAMENTO MOBILE: Recebe o gatilho de SOS/Pânico do técnico em rota
    @PostMapping("/{id}/panico")
    public ResponseEntity<Void> dispararAlertaPanico(@PathVariable Long id) {
        service.dispararAlertaPanico(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.ok().build();
    }

    private void publicarMudancaStatus(OrdemDeServico ordem) {
        if (ordem == null || ordem.getId() == null) {
            return;
        }

        messagingTemplate.convertAndSend("/topic/ordens/" + ordem.getId(), ordem);

        if (ordem.getClienteId() != null) {
            messagingTemplate.convertAndSend("/topic/solicitacoes/" + ordem.getClienteId(), ordem);
        }
        if (ordem.getEmpresaId() != null) {
            messagingTemplate.convertAndSend("/topic/empresa/" + ordem.getEmpresaId(), ordem);
        }
        if (ordem.getFuncionario() != null && !ordem.getFuncionario().isBlank()) {
            messagingTemplate.convertAndSend("/topic/tecnico/" + ordem.getFuncionario(), ordem);
        }
    }
    
    @PutMapping("/{id}/aceitar")
    public ResponseEntity<OrdemDeServico> aceitarOrdem(
            @PathVariable Long id,
            @RequestParam Long funcionarioId,
            @RequestParam String data,
            @RequestParam String status) {

        // Recupera a OS existente usando a regra de negócio do Service
        OrdemDeServico ordem = service.buscarPorId(id);

        // Aloca os novos parâmetros de sincronização móvel tática
        ordem.setFuncionario(String.valueOf(funcionarioId));
        ordem.setDataAgendamento(data);
        ordem.setStatus(status);

        // Salva e atualiza o estado no repositório de dados
        OrdemDeServico updated = service.salvar(ordem);

        // Dispara a alteração em tempo real via WebSocket/SimpMessagingTemplate para o App
        publicarMudancaStatus(updated);

        return ResponseEntity.ok(updated);
    }
}