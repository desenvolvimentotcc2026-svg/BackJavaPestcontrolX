package com.dedetizacao.app.dedetizacao.Service;

import java.util.List;
import java.util.Optional; // Importante para o AuthController

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Exception.ResourceNotFoundException;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final EmpresaRepository empresaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(EmpresaRepository empresaRepository, FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado!"));
    }

    public void deletar(Long id) {
        funcionarioRepository.deleteById(id);
    }

    public Funcionario salvar(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario salvar(FuncionarioDto dto, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setCargo(dto.getCargo());
        funcionario.setAtivo(dto.getAtivo());
        funcionario.setCpf(dto.getCpf());
        funcionario.setEmpresa(empresa);

        return funcionarioRepository.save(funcionario);
    }

    public Funcionario atualizar(Long id, Funcionario funcionarioAtualizado) {
        Funcionario funcionario = buscarPorId(id);

        funcionario.setNome(funcionarioAtualizado.getNome());
        funcionario.setCargo(funcionarioAtualizado.getCargo());

        return funcionarioRepository.save(funcionario);
    }

    public FuncionarioDto toDTO(Funcionario funcionario) {
        FuncionarioDto dto = new FuncionarioDto();
        dto.setId(funcionario.getId());
        dto.setNome(funcionario.getNome());
        dto.setEmail(funcionario.getEmail());
        dto.setTelefone(funcionario.getTelefone());
        dto.setCargo(funcionario.getCargo());
        dto.setAtivo(funcionario.getAtivo());
        dto.setCpf(funcionario.getCpf());

        if (funcionario.getEmpresa() != null) {
            dto.setEmpresa_id(funcionario.getEmpresa().getId());
        }

        return dto;
    }

    public void atualizarStatus(Long id, String novoStatus) {
        // Busca o funcionário pelo ID (usando o método que você já deve ter ou direto do repository)
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado!"));

        // Altera o status (certifique-se de que o atributo 'status' existe na sua Model Funcionario)
        funcionario.setStatus(novoStatus);

        // Salva a alteração no banco de dados
        funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> listarPorStatus(String status) {
        // Certifique-se de que seu FuncionarioRepository tem o método findByStatus
        return funcionarioRepository.findByStatus(status);
    }

    public List<Funcionario> listarPorEmpresa(Long empresaId){
        return funcionarioRepository.findByEmpresaId(empresaId);
    }


    public Optional<Funcionario> buscarPorEmail(String email) {
        return funcionarioRepository.findByEmail(email);
    }
}