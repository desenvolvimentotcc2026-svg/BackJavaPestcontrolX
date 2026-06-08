package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class FuncionarioService {

    private final FuncionarioRepository repo;
    private final EmpresaRepository empresaRepo;

    public FuncionarioService(FuncionarioRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
    }

    public Funcionario criar(RegisterRequest req, Long usuarioId) {

        Empresa empresa = empresaRepo.findById(Long.valueOf(req.getCnpj()))
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Funcionario f = new Funcionario();
        f.setNome(req.getNome());
        f.setEmail(req.getEmail());
        f.setCpf(req.getCnpj());
        f.setEmpresa(empresa);

        return repo.save(f);
    }

    public List<Funcionario> listarPorEmpresa(Long id) {
        return repo.findByEmpresaId(id);
    }
}