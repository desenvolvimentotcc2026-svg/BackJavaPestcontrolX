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

    public OrdemDeServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem não encontrada"));
    }

    public List<OrdemDeServico> listarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaIdOrderByIdDesc(empresaId);
    }

    public List<OrdemDeServico> listarPorCliente(Long clienteId) {
        return repository.findByClienteIdOrderByIdDesc(clienteId);
    }

    public List<OrdemDeServico> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(String.valueOf(funcionarioId));
    }

    public List<OrdemDeServico> listarPorStatus(String status) {
        return repository.findByStatus(status);
    }

    public OrdemDeServico salvar(OrdemDeServico ordem) {
        if (ordem.getStatus() == null) ordem.setStatus("PENDENTE");
        if (ordem.getDataAbertura() == null) ordem.setDataAbertura(LocalDateTime.now());
        return repository.save(ordem);
    }

    public OrdemDeServico aceitar(Long id, Long funcionarioId) {
        OrdemDeServico o = buscarPorId(id);
        o.setStatus("ACEITA");
        o.setFuncionario(String.valueOf(funcionarioId));
        return repository.save(o);
    }

    public OrdemDeServico iniciar(Long id) {
        OrdemDeServico o = buscarPorId(id);
        o.setStatus("EM_ROTA");
        return repository.save(o);
    }

    public OrdemDeServico finalizar(Long id, OrdemDeServico dados) {
        OrdemDeServico o = buscarPorId(id);
        o.setStatus("FINALIZADA");
        o.setDataFinalizacao(LocalDateTime.now());
        return repository.save(o);
    }

    public OrdemDeServico atualizarGps(Long id, Double lat, Double lng) {
        OrdemDeServico o = buscarPorId(id);
        o.setLatitude(lat);
        o.setLongitude(lng);
        return repository.save(o);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public List<OrdemDeServico> listarAtivasPorCliente(Long id) {
        return repository.findAtivasByClienteId(id);
    }

    public List<OrdemDeServico> listarAtivasPorEmpresa(Long id) {
        return repository.findAtivasByEmpresaId(id);
    }
}