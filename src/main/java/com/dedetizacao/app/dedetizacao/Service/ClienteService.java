package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public Cliente salvar(Cliente c) {
        return repository.save(c);
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente atualizar(Long id, Cliente novo) {
        Cliente c = buscarPorId(id);
        c.setNome(novo.getNome());
        c.setEmail(novo.getEmail());
        return repository.save(c);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // fluxo register LIMPO
    public Cliente criarFromRegister(RegisterRequest req, Long empresaId) {
        Cliente c = new Cliente();
        c.setNome(req.getNome());
        c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone());

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        c.setEmpresa(empresa);

        return repository.save(c);
    }
}