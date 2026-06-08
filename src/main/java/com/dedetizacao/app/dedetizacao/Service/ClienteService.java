package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          EmpresaRepository empresaRepository) {
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // 🔥 CORREÇÃO: Adicionado o método exigido pelo AuthController
    public void salvarFromRegister(RegisterRequest req, Long usuarioId) {
        Cliente cliente = new Cliente();
        cliente.setNome(req.getNome());
        cliente.setEmail(req.getEmail());
        cliente.setTelefone("Não informado");
        clienteRepository.save(cliente);
    }

    public Cliente salvar(ClienteDto dto, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmpresa(empresa);

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id);

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(cliente);
    }

    // 🔥 CORREÇÃO: Sobrecarga para o ClienteController que está passando (empresaId, clienteId)
    public Cliente atualizar(Long empresaId, Long clienteId, Cliente clienteAtualizado) {
        return atualizar(clienteId, clienteAtualizado);
    }

    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }

    // 🔥 CORREÇÃO: Sobrecarga para o ClienteController que está passando (empresaId, clienteId)
    public void deletar(Long empresaId, Long clienteId) {
        deletar(clienteId);
    }

    public ClienteDto toDTO(Cliente cliente) {
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        return dto;
    }
}