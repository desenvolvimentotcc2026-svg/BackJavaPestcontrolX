package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
import com.dedetizacao.app.dedetizacao.Dto.EnderecoClienteDto;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.EnderecoCliente;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Importante para o AuthController

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

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente salvar(ClienteDto dto, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setEmpresa(empresa);

        if (dto.getEndereco() != null) {
            EnderecoCliente endereco = new EnderecoCliente();
            endereco.setCep(dto.getEndereco().getCep());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setRua(dto.getEndereco().getRua());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setCliente(cliente);

            cliente.setEndereco(endereco);
        }

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public void deletar(long id, Long clienteId) {
        clienteRepository.deleteById(id);
    }

    public Cliente atualizar(Long empresaId, Long clienteId, Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());
        cliente.setEmail(clienteAtualizado.getEmail());

        return clienteRepository.save(cliente);
    }

    public ClienteDto toDTO(Cliente cliente) {
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmpresaId(cliente.getEmpresa() != null ? cliente.getEmpresa().getId() : null);

        if (cliente.getEndereco() != null) {
            EnderecoClienteDto enderecoDto = new EnderecoClienteDto();
            enderecoDto.setCep(cliente.getEndereco().getCep());
            enderecoDto.setCidade(cliente.getEndereco().getCidade());
            enderecoDto.setBairro(cliente.getEndereco().getBairro());
            enderecoDto.setRua(cliente.getEndereco().getRua());
            enderecoDto.setNumero(cliente.getEndereco().getNumero());

            dto.setEndereco(enderecoDto);
        }

        return dto;
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }
}