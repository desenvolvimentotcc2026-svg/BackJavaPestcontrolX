package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    // AGORA ESTÁ PREENCHIDO E FUNCIONANDO
    public Empresa salvarFromRegister(RegisterRequest req, Long usuarioId) {
        Empresa e = new Empresa();
        e.setUsuarioId(usuarioId);

        // PREENCHENDO OS CAMPOS OBRIGATÓRIOS
        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setSenha(req.getSenha()); // A senha deve ser a mesma do usuário
        e.setCnpj(req.getCnpj());

        return repository.save(e);
    }
}