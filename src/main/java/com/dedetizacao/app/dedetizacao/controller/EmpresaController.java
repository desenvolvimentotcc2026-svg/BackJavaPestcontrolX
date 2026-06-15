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
        return ResponseEntity.ok(service.salvar(empresa));
    }

    @GetMapping
    public List<Empresa> listar() {
        return service.listarTodos();
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

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @RequestBody Empresa empresa) {
        return repository.findById(id)
                .map(existente -> {
                    if (empresa.getNome() != null) existente.setNome(empresa.getNome());
                    if (empresa.getSobre() != null) existente.setSobre(empresa.getSobre());
                    if (empresa.getMensagemAutomatica() != null) existente.setMensagemAutomatica(empresa.getMensagemAutomatica());

                    // SALVANDO OS NOVOS DADOS
                    if (empresa.getContatoPlantao() != null) existente.setContatoPlantao(empresa.getContatoPlantao());
                    if (empresa.getJanelaAtendimento() != null) existente.setJanelaAtendimento(empresa.getJanelaAtendimento());
                    if (empresa.getLicencaSanitaria() != null) existente.setLicencaSanitaria(empresa.getLicencaSanitaria());
                    if (empresa.getResponsavelTecnico() != null) existente.setResponsavelTecnico(empresa.getResponsavelTecnico());
                    if (empresa.getEspecialidades() != null) existente.setEspecialidades(empresa.getEspecialidades());

                    if (empresa.getEndereco() != null) {
                        existente.setEndereco(empresa.getEndereco());
                    }

                    Empresa salva = service.salvar(existente);
                    System.out.println("✅ Empresa ID " + id + " atualizada com sucesso!");
                    return ResponseEntity.ok(salva);
                })
                .orElseGet(() -> {
                    System.out.println("❌ Alerta: Tentativa de atualizar Empresa ID " + id + ", mas ela NÃO existe no banco de dados.");
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/{id}")
    public ResponseEntity<Empresa> atualizarPost(@PathVariable Long id, @RequestBody Empresa empresa) {
        return atualizar(id, empresa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        try {
            Empresa empresa = service.buscarPorId(id);
            return ResponseEntity.ok(empresa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}