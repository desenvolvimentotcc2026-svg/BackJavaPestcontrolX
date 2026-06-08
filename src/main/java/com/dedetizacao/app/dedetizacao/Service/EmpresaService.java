package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository repo;

    public EmpresaService(EmpresaRepository repo) {
        this.repo = repo;
    }

    public Empresa salvarFromRegister(RegisterRequest req, Long usuarioId) {

        Empresa e = new Empresa();
        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setCnpj(req.getCnpj());

        return repo.save(e);
    }

    public List<Empresa> listarTodos() {
        return repo.findAll();
    }

    public Empresa buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }

    public Optional<Empresa> buscarPorEmail(String email) {
        return repo.findByEmail(email);
    }
}