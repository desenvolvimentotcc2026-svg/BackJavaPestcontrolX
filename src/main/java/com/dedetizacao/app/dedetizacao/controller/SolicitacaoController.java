package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Repository.SolicitacaoRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes") // Centraliza de forma exclusiva o endpoint do App Móvel
@CrossOrigin(origins = "*")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Criação de nova solicitação pelo cliente via App com os parâmetros estruturados
    @PostMapping
    public ResponseEntity<OrdemDeServico> criarSolicitacao(
            @RequestBody OrdemDeServico solicitacao,
            @RequestParam("empresa_id") Long empresaId,
            @RequestParam(value = "cliente_id", required = false) Long clienteId
    ) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        solicitacao.setEmpresa(empresa);
        solicitacao.setStatus("PENDENTE");

        if (clienteId != null) {
            Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
            if (cliente != null) {
                solicitacao.setCliente(cliente);
            }
        }

        if (solicitacao.getDataAgendamento() == null) {
            String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
            solicitacao.setDataAgendamento(dataHoje);
        }

        OrdemDeServico salva = repository.save(solicitacao);
        return ResponseEntity.ok(salva);
    }

    // Listagem consumida pelo Painel Administrativo Web
    @GetMapping
    public List<OrdemDeServico> listarTodas() {
        return repository.findAll();
    }

    // Busca filtrada pela Agenda do Aplicativo Android
    @GetMapping("/data/{data}")
    public List<OrdemDeServico> buscarPorData(@PathVariable String data) {
        String dataFormatada = data.replace("-", "/");
        return repository.findByDataAgendamento(dataFormatada);
    }
}