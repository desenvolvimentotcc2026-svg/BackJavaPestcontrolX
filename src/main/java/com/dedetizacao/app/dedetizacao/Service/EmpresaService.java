package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    public Empresa salvar(Empresa e) {
        return repository.save(e);
    }

    public List<Empresa> listarTodos() {
        return repository.findAll();
    }

    public Empresa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Empresa criar(Long usuarioId) {
        Empresa e = new Empresa();
        e.setUsuarioId(usuarioId);
        return repository.save(e);
    }

    // MÉTODO ADICIONADO PARA CORRIGIR O ERRO DE COMPILAÇÃO
    public Empresa salvarFromRegister(RegisterRequest req, Long usuarioId) {
        Empresa e = new Empresa();
        e.setUsuarioId(usuarioId);
        // Se precisar setar mais campos da empresa vindo do RegisterRequest,
        // use os métodos setters aqui. Exemplo:
        // e.setNome(req.getNome());

        return repository.save(e);
    }
}