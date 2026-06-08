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

    private final ClienteRepository repo;
    private final EmpresaRepository empresaRepo;

    public ClienteService(ClienteRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
    }

    public Cliente salvar(Cliente cliente) {

        Empresa empresa = null;

        if (cliente.getEmpresaId() != null) {
            empresa = empresaRepository.findById(cliente.getEmpresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        }

        cliente.setEmpresa(empresa);

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return repo.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        Cliente c = buscarPorId(id);
        c.setNome(cliente.getNome());
        c.setEmail(cliente.getEmail());
        c.setTelefone(cliente.getTelefone());
        return repo.save(c);
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }

    public ClienteDto toDTO(Cliente c) {
        ClienteDto dto = new ClienteDto();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setEmail(c.getEmail());
        dto.setTelefone(c.getTelefone());
        return dto;
    }

    public void vincularCliente(Long clienteId, Long empresaId) {
        Cliente c = repo.findById(clienteId).orElseThrow();
        Empresa e = empresaRepo.findById(empresaId).orElseThrow();

        c.setEmpresa(e);
        repo.save(c);
    }
}