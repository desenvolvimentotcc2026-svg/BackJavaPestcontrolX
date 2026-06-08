package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<Empresa> listarTodos() {
        return empresaRepository.findAll();
    }

    public Empresa salvar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    public void deletar(Long id) {
        empresaRepository.deleteById(id);
    }

    public Empresa atualizar(Long id, Empresa empresaAtualizada) {
        Empresa empresa = buscarPorId(id);

        empresa.setCnpj(empresaAtualizada.getCnpj());
        empresa.setEmail(empresaAtualizada.getEmail());
        empresa.setNome(empresaAtualizada.getNome());

        return empresaRepository.save(empresa);
    }
}