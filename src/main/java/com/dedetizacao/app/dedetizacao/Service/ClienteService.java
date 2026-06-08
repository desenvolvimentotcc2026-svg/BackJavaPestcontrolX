package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
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

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente criarFromDto(ClienteDto dto, Long empresaId) {
        Cliente c = new Cliente();
        c.setNome(dto.getNome());
        c.setEmail(dto.getEmail());
        c.setTelefone(dto.getTelefone());

        if (empresaId != null) {
            Empresa empresa = empresaRepository.findById(empresaId).orElse(null);
            c.setEmpresa(empresa);
        }

        return clienteRepository.save(c);
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());
        return clienteRepository.save(cliente);
    }

    public void deletar(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
        }
    }
}