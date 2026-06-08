package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Repository.OrdemDeServicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<OrdemDeServico> listarPorCliente(Long id) {
        return repository.findByClienteIdOrderByIdDesc(id);
    }

    public List<OrdemDeServico> listarPorEmpresa(Long id) {
        return repository.findByEmpresaIdOrderByIdDesc(id);
    }

    public List<OrdemDeServico> listarAtivasPorCliente(Long id) {
        return repository.findAtivasByClienteId(id);
    }

    public List<OrdemDeServico> listarAtivasPorEmpresa(Long id) {
        return repository.findAtivasByEmpresaId(id);
    }

    public OrdemDeServico salvar(OrdemDeServico o) {
        if (o.getStatus() == null) o.setStatus("PENDENTE");
        if (o.getDataAbertura() == null) o.setDataAbertura(LocalDateTime.now());
        return repository.save(o);
    }
}

