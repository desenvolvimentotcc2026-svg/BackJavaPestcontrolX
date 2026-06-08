package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repo;
    private final EmpresaRepository empresaRepo;

    public FuncionarioService(FuncionarioRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
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
        f.setTelefone(dto.getTelefone());
        f.setCpf(dto.getCpf());
        f.setCargo(dto.getCargo());
        f.setEmpresa(empresa);

        return repo.save(f);
    }

    public Funcionario atualizar(Long id, Funcionario f) {
        Funcionario atual = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Não encontrado"));

        atual.setNome(f.getNome());
        atual.setCargo(f.getCargo());

        return repo.save(atual);
    }

    public void atualizarStatus(Long id, String status) {
        Funcionario f = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Não encontrado"));

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