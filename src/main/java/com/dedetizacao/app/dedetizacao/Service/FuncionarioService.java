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
        f.setCpf(req.getCnpj()); // Vincula o documento enviado pelo app mobile para evitar erro de banco NotNull
        f.setUsuarioId(usuarioId);
        f.setAtivo(true);
        f.setStatus("OFFLINE");
        return repo.save(f);
    }

    public List<Funcionario> listarTodos() {
        return repo.findAll();
    }

    // 🟢 MÉTODO ADICIONADO: Corrige o erro de "cannot find symbol" resolvendo a deleção
    public void deletarPorId(long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado.");
        }
        repo.deleteById(id);
    }

    public void atualizarStatus(Long id, String status) {
        Funcionario f = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
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
        Long idEmpresaFinal = (empresaId != null) ? empresaId : dto.getEmpresa_id();
        if (idEmpresaFinal == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da empresa é obrigatório.");
        }

        Empresa empresa = empresaRepo.findById(idEmpresaFinal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa informada não existe"));

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setEmail(dto.getEmail());
        f.setTelefone(dto.getTelefone());
        f.setCargo(dto.getCargo());
        f.setCpf(dto.getCpf());
        f.setAtivo(dto.getAtivo());
        f.setEmpresa(empresa);
        return repo.save(f);
    }

    public Funcionario atualizar(Long id, FuncionarioDto dto) {
        Funcionario existente = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        if (dto.getNome() != null) existente.setNome(dto.getNome());
        if (dto.getEmail() != null) existente.setEmail(dto.getEmail());
        if (dto.getTelefone() != null) existente.setTelefone(dto.getTelefone());
        if (dto.getCargo() != null) existente.setCargo(dto.getCargo());
        if (dto.getAtivo() != null) existente.setAtivo(dto.getAtivo());

        return repo.save(existente);
    }
}