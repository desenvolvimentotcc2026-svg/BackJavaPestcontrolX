package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repo;
    private final EmpresaRepository empresaRepo;

    public FuncionarioService(FuncionarioRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
    }

    // 🔥 O MÉTODO QUE ESTAVA FALTANDO
    public Funcionario criar(RegisterRequest req, Long usuarioId) {

        Empresa empresa = empresaRepo.findById(Long.valueOf(req.getCnpj()))
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario f = new Funcionario();
        f.setNome(req.getNome());
        f.setEmail(req.getEmail());
        f.setCpf(req.getCnpj());
        f.setEmpresa(empresa);

        return repo.save(f);
    }

    public List<Funcionario> listarTodos() {
        return repo.findAll();
    }

    public Funcionario salvar(FuncionarioDto dto, Long empresaId) {
        Empresa empresa = empresaRepo.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setEmail(dto.getEmail());
        f.setCpf(dto.getCpf());
        f.setEmpresa(empresa);

        return repo.save(f);
    }

    public Funcionario atualizar(Long id, Funcionario f) {
        Funcionario atual = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        atual.setNome(f.getNome());
        atual.setCargo(f.getCargo());

        return repo.save(atual);
    }

    public void atualizarStatus(Long id, String status) {
        Funcionario f = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

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
        dto.setCpf(f.getCpf());
        return dto;
    }
}