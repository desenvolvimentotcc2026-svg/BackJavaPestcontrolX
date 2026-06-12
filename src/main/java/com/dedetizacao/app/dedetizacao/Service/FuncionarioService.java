package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        Funcionario f = new Funcionario();
        f.setNome(req.getNome());
        f.setEmail(req.getEmail());
        return repo.save(f);
    }

    public List<Funcionario> listarTodos() {
        return repo.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
    }

    public Funcionario atualizar(Long id, FuncionarioDto dto) {
        Funcionario func = buscarPorId(id);
        func.setNome(dto.getNome());
        func.setEmail(dto.getEmail());
        func.setTelefone(dto.getTelefone());
        func.setCargo(dto.getCargo());
        func.setCpf(dto.getCpf());
        if (dto.getAtivo() != null) {
            func.setAtivo(dto.getAtivo());
        }

        if (dto.getEmpresa_id() != null) {
            Empresa em = empresaRepo.findById(dto.getEmpresa_id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa de destino não encontrada"));
            func.setEmpresa(em);
        }
        return repo.save(func);
    }

    public void atualizarStatus(Long id, String status) {
        Funcionario f = buscarPorId(id);
        // Limpa as aspas inseridas pelo formato de requisições JSON brutas
        if (status != null) {
            status = status.replace("\"", "").trim();
        }
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
        dto.setCargo(f.getCargo());
        dto.setCpf(f.getCpf());
        dto.setAtivo(f.getAtivo());
        if (f.getEmpresa() != null) {
            dto.setEmpresa_id(f.getEmpresa().getId());
        }
        return dto;
    }

    public Funcionario salvar(FuncionarioDto dto, Long empresaId) {
        // Fallback inteligente: aceita o ID da URL ou do corpo da requisição
        Long idEmpresaFinal = (empresaId != null) ? empresaId : dto.getEmpresa_id();
        if (idEmpresaFinal == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da empresa vinculada é obrigatório.");
        }

        Empresa empresa = empresaRepo.findById(idEmpresaFinal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa informada não existe"));

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setEmail(dto.getEmail());
        f.setTelefone(dto.getTelefone());
        f.setCargo(dto.getCargo());
        f.setCpf(dto.getCpf()); // Correção do estouro de Not-Null no banco
        f.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        f.setStatus("OFFLINE");
        f.setEmpresa(empresa);

        return repo.save(f);
    }
}