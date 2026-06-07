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

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<OrdemDeServico>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrdemDeServico>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<OrdemDeServico> aceitarOrdem(
            @PathVariable Long id,
            @RequestParam(value = "funcionarioId", required = false) Long funcionarioId) {
        OrdemDeServico atualizada = service.aceitar(id, funcionarioId);
        publicarMudancaStatus(atualizada);
        return ResponseEntity.ok(atualizada);
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<OrdemDeServico> iniciarOrdem(@PathVariable Long id) {
        OrdemDeServico atualizada = service.iniciar(id);
        publicarMudancaStatus(atualizada);
        return ResponseEntity.ok(atualizada);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<OrdemDeServico> finalizarOrdem(
            @PathVariable Long id,
            @RequestBody(required = false) OrdemDeServico dadosFinalizacao) {
        OrdemDeServico atualizada = service.finalizar(id, dadosFinalizacao);
        publicarMudancaStatus(atualizada);
        return ResponseEntity.ok(atualizada);
    }

    @PutMapping("/{id}/gps")
    public ResponseEntity<OrdemDeServico> atualizarGps(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        OrdemDeServico atualizada = service.atualizarGps(id, latitude, longitude);
        String payloadGps = String.format("{\"ordemId\":%d,\"latitude\":%f,\"longitude\":%f}", id, latitude, longitude);
        messagingTemplate.convertAndSend("/topic/gps/" + id, payloadGps);
        publicarMudancaStatus(atualizada);
        return ResponseEntity.ok(atualizada);
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
}