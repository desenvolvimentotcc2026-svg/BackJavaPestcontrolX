package com.dedetizacao.app.dedetizacao.controller;

import java.util.List;
import java.security.Principal;
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

    // 🔥 NOVA ROTA INTELIGENTE: Atualiza a empresa usando apenas o Token JWT
    @PutMapping("/perfil")
    public ResponseEntity<Empresa> atualizarPerfilLogado(Principal principal, @RequestBody Empresa empresa) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String emailLogado = principal.getName();

        return repository.findByEmail(emailLogado)
                .map(existente -> {
                    if (empresa.getNome() != null) existente.setNome(empresa.getNome());
                    if (empresa.getSobre() != null) existente.setSobre(empresa.getSobre());
                    if (empresa.getMensagemAutomatica() != null) existente.setMensagemAutomatica(empresa.getMensagemAutomatica());

                    // Atualização dos novos campos mapeados do formulário
                    if (empresa.getContatoPlantao() != null) existente.setContatoPlantao(empresa.getContatoPlantao());
                    if (empresa.getJanelaAtendimento() != null) existente.setJanelaAtendimento(empresa.getJanelaAtendimento());
                    if (empresa.getLicencaSanitaria() != null) existente.setLicencaSanitaria(empresa.getLicencaSanitaria());
                    if (empresa.getResponsavelTecnico() != null) existente.setResponsavelTecnico(empresa.getResponsavelTecnico());
                    if (empresa.getEspecialidades() != null) existente.setEspecialidades(empresa.getEspecialidades());

                    if (empresa.getEndereco() != null) {
                        existente.setEndereco(empresa.getEndereco());
                    }

                    Empresa salva = service.salvar(existente);
                    System.out.println("✅ Perfil da Empresa (" + emailLogado + ") sincronizado e atualizado!");
                    return ResponseEntity.ok(salva);
                })
                .orElseGet(() -> {
                    System.out.println("❌ Alerta: Email " + emailLogado + " autenticado, mas não localizado na tabela Empresa.");
                    return ResponseEntity.notFound().build();
                });
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

    @GetMapping("/perfil")
    public ResponseEntity<Empresa> obterPerfilLogado(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return repository.findByEmail(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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