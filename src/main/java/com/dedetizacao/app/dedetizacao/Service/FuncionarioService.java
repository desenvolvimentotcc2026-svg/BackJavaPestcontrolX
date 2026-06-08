package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final EmpresaRepository empresaRepository;

    public FuncionarioService(EmpresaRepository empresaRepository, FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public Funcionario salvar(FuncionarioDto dto, Long empresaId) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setEmail(dto.getEmail());
        f.setTelefone(dto.getTelefone());
        f.setCargo(dto.getCargo());
        f.setAtivo(dto.getAtivo());
        f.setCpf(dto.getCpf());
        f.setEmpresa(empresa);

        return funcionarioRepository.save(f);
    }

    public Funcionario atualizar(Long id, Funcionario novo) {
        Funcionario f = buscarPorId(id);

        f.setNome(novo.getNome());
        f.setCargo(novo.getCargo());

        return funcionarioRepository.save(f);
    }

    public void atualizarStatus(Long id, String status) {
        Funcionario f = buscarPorId(id);
        f.setStatus(status);
        funcionarioRepository.save(f);
    }

    public List<Funcionario> listarPorStatus(String status) {
        return funcionarioRepository.findByStatus(status);
    }

    public List<Funcionario> listarPorEmpresa(Long empresaId) {
        return funcionarioRepository.findByEmpresaId(empresaId);
    }

    public Optional<Funcionario> buscarPorEmail(String email) {
        return funcionarioRepository.findByEmail(email);
    }

    public FuncionarioDto toDTO(Funcionario f) {
        FuncionarioDto dto = new FuncionarioDto();

        dto.setId(f.getId());
        dto.setNome(f.getNome());
        dto.setEmail(f.getEmail());
        dto.setTelefone(f.getTelefone());
        dto.setCargo(f.getCargo());
        dto.setAtivo(f.getAtivo());
        dto.setCpf(f.getCpf());

        if (f.getEmpresa() != null) {
            dto.setEmpresa_id(f.getEmpresa().getId());
        }

        return dto;
    }
}