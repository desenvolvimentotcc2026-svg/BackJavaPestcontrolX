package com.dedetizacao.app.dedetizacao.controller;

import java.util.List;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaRepository repository;
    private final EmpresaService service;

    public EmpresaController(EmpresaRepository repository, EmpresaService service) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Empresa> criar(@RequestBody Empresa empresa) {
        return ResponseEntity.ok(service.Salvar(empresa));
    }

    @GetMapping
    public List<Empresa> listar() {
        return service.listartodos();
    }

    @GetMapping("/busca")
    public List<Empresa> buscarPorNome(@RequestParam String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Empresa> buscarPorCnpj(@PathVariable String cnpj) {
        return repository.findByCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @RequestBody Empresa empresa) {
        return repository.findById(id)
                .map(existente -> {
                    if (empresa.getNome() != null) existente.setNome(empresa.getNome());
                    if (empresa.getEmail() != null) existente.setEmail(empresa.getEmail());
                    if (empresa.getCnpj() != null) existente.setCnpj(empresa.getCnpj());
                    if (empresa.getSobre() != null) existente.setSobre(empresa.getSobre());
                    if (empresa.getMensagemAutomatica() != null) existente.setMensagemAutomatica(empresa.getMensagemAutomatica());
                    if (empresa.getEndereco() != null) existente.setEndereco(empresa.getEndereco());

                    return ResponseEntity.ok(service.Salvar(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Empresa> atualizarPost(@PathVariable Long id, @RequestBody Empresa empresa) {
        return atualizar(id, empresa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        try {
            Empresa empresa = service.buscarporid(id);
            return ResponseEntity.ok(empresa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}