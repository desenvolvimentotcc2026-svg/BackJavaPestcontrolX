package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Repository.OrdemDeServicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrdemDeServicoService {
    private final OrdemDeServicoRepository repository;

    public OrdemDeServicoService(OrdemDeServicoRepository repository) {
        this.repository = repository;
    }

    public List<OrdemDeServico> listar() {
        return repository.findAll();
    }

    // Usado pela Empresa no Site Vercel
    public List<OrdemDeServico> listarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaId(empresaId);
    }

    // Usado pelo Cliente no App
    public List<OrdemDeServico> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    // Usado pelo Funcionário/Técnico no App
    public List<OrdemDeServico> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(funcionarioId);
    }

    public OrdemDeServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem # " + id + " não encontrada!"));
    }

    public OrdemDeServico salvar(OrdemDeServico ordem) {
        if (ordem.getStatus() == null) {
            ordem.setStatus("PENDENTE");
        }
        return repository.save(ordem);
    }

    public OrdemDeServico atualizar(Long id, OrdemDeServico dadosNovos) {
        OrdemDeServico existente = buscarPorId(id);
        existente.setStatus(dadosNovos.getStatus());
        existente.setData(dadosNovos.getData());
        existente.setFuncionario(dadosNovos.getFuncionario());
        // Adicione outros campos conforme necessário
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}