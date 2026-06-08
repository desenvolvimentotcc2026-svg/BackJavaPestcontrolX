package com.dedetizacao.app.dedetizacao.Service;

import java.util.List;
import java.util.Optional; // Importante para o AuthController
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<Empresa> listartodos() {
        return empresaRepository.findAll();
    }

    public Empresa Salvar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa salvarFromRegister(RegisterRequest req, Long usuarioId) {
        Empresa e = new Empresa();
        e.setNome(req.getNome());
        e.setCnpj(req.getCnpj());
        e.setEmail(req.getEmail());
        return empresaRepository.save(e);
    }

    public Empresa buscarporid(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao encontrar a empresa!"));
    }

    public void deletar(long id) {
        empresaRepository.deleteById(id);
    }

    public Empresa atualizar(long id, Empresa empresaatualizada) {
        Empresa empresa = buscarporid(id);

        empresa.setCnpj(empresaatualizada.getCnpj());
        empresa.setEmail(empresaatualizada.getEmail());
        empresa.setNome(empresaatualizada.getNome());

        return empresaRepository.save(empresa);
    }

    public boolean validarCredenciais(String cnpj, String chave) {
        // Tenta encontrar a empresa. Se retornar presente, validou!
        return empresaRepository.findByCnpjAndChaveCorporativa(cnpj, chave).isPresent();
    }

    public Optional<Empresa> buscarPorEmail(String email) {
        return empresaRepository.findByEmail(email);
    }
}