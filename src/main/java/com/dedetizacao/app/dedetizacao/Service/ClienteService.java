package com.dedetizacao.app.dedetizacao.Service;

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

    // ✔ usado pelo AuthController (REGISTER)
    public Cliente criarFromRegister(String nome, String email, String telefone, Long empresaId) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone(telefone);
        c.setEmpresa(empresa);

        return repository.save(c);
    }
}