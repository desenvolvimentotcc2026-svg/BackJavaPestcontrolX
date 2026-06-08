package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

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

    // 🔥 REGISTRO SIMPLES
    public Cliente criar(Long usuarioId, RegisterRequest req) {
        Cliente c = new Cliente();
        c.setUsuarioId(usuarioId);
        c.setNome(req.getNome());
        c.setEmail(req.getEmail());
        return repository.save(c);
    }
}