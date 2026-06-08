package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
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

    public Cliente salvarFromRegister(RegisterRequest req, Long usuarioId) {

        Empresa empresa = empresaRepo.findById(Long.valueOf(req.getCnpj()))
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Cliente c = new Cliente();
        c.setNome(req.getNome());
        c.setEmail(req.getEmail());
        c.setEmpresa(empresa);

        return repo.save(c);
    }

    public List<Cliente> listarTodos() {
        return repo.findAll();
    }
}