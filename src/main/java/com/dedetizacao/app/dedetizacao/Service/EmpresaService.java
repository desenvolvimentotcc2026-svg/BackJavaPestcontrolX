package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    public Empresa criar(RegisterRequest req, Long usuarioId) {

        Empresa e = new Empresa();
        e.setNome(req.getNome());
        e.setCnpj(req.getCnpj());
        e.setUsuarioId(usuarioId);

        return repository.save(e);
    }
}