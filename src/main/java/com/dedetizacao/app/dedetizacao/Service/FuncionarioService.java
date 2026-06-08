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

    public Funcionario criar(RegisterRequest req, Long usuarioId) {

        Empresa empresa = empresaRepo.findById(Long.valueOf(req.getCnpj()))
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario f = new Funcionario();
        f.setNome(req.getNome());
        f.setEmail(req.getEmail());
        f.setCpf(req.getCnpj());

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
        func.setCargo(f.getCargo());
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

    // 🔥 Método exigido pelo FuncionarioController com as chaves fechadas corretamente
    public void salvar(FuncionarioDto dto, Long empresaId) {
        // Futura implementação de salvamento de DTO
    }
}