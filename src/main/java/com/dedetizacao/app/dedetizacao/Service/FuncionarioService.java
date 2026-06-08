package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    // 🔥 CORREÇÃO 1: Unificamos o nome para "repo" e usamos o construtor corretamente
    private final FuncionarioRepository repo;
    private final EmpresaRepository empresaRepo;

    public FuncionarioService(FuncionarioRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
    }

    // 🔥 CORREÇÃO 2: A ordem dos parâmetros ajustada para casar com o AuthController
    public Funcionario criar(RegisterRequest req, Long usuarioId) {
        Funcionario f = new Funcionario();
        // Verifique se a sua Model Funcionario usa setUsuarioId ou setEmpresa.
        // Baseado no seu código, mantive o que você escreveu.
        f.setNome(req.getNome());
        f.setEmail(req.getEmail());

        return repo.save(f);
    }

    public List<Funcionario> listarTodos() {
        return repo.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public Funcionario atualizar(Long id, Funcionario f) {
        Funcionario func = buscarPorId(id);
        func.setNome(f.getNome());
        func.setEmail(f.getEmail());
        func.setTelefone(f.getTelefone());
        func.setStatus(f.getStatus());
        return repo.save(func);
    }

    public void atualizarStatus(Long id, String status) {
        Funcionario f = buscarPorId(id);
        f.setStatus(status);
        repo.save(f);
    }

    public List<Funcionario> listarPorStatus(String status) {
        return repo.findByStatus(status);
    }

    public FuncionarioDto toDTO(Funcionario f) {
        FuncionarioDto dto = new FuncionarioDto();
        dto.setId(f.getId());
        dto.setNome(f.getNome());
        dto.setEmail(f.getEmail());
        dto.setTelefone(f.getTelefone());
        return dto;
    }

    public Funcionario salvar(FuncionarioDto dto, Long empresaId) {

        Empresa empresa = empresaRepo.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setEmail(dto.getEmail());
        f.setTelefone(dto.getTelefone());
        f.setStatus("OFFLINE");
        f.setEmpresa(empresa);

        return repo.save(f);
    }

    public Funcionario criarFromRegister(RegisterRequest req, Long empresaId) {
        Funcionario f = new Funcionario();
        f.setNome(req.getNome());
        f.setEmail(req.getEmail());
        // Caso a sua Model Funcionario não tenha "setEmpresaId", você precisará buscar
        // a Empresa via empresaRepo.findById(empresaId) e usar f.setEmpresa(empresa);

        return repo.save(f);
    }
}