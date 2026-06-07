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

    public List<OrdemDeServico> listarPorEmpresa(Long empresaId) {
        return repository.findByEmpresaIdOrderByIdDesc(empresaId);
    }

    public List<OrdemDeServico> listarAtivasPorEmpresa(Long empresaId) {
        return repository.findAtivasByEmpresaId(empresaId);
    }

    public List<OrdemDeServico> listarPorCliente(Long clienteId) {
        return repository.findByClienteIdOrderByIdDesc(clienteId);
    }

    public List<OrdemDeServico> listarAtivasPorCliente(Long clienteId) {
        return repository.findAtivasByClienteId(clienteId);
    }

    public List<OrdemDeServico> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(String.valueOf(funcionarioId));
    }

    public List<OrdemDeServico> listarPorStatus(String status) {
        return repository.findByStatus(status);
    }

    public OrdemDeServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem #" + id + " nao encontrada."));
    }

    public OrdemDeServico salvar(OrdemDeServico ordem) {
        if (ordem.getStatus() == null || ordem.getStatus().isBlank()) {
            ordem.setStatus("PENDENTE");
        }
        if (ordem.getDataAbertura() == null) {
            ordem.setDataAbertura(LocalDateTime.now());
        }
        return repository.save(ordem);
    }

    public OrdemDeServico aceitar(Long id, Long funcionarioId) {
        OrdemDeServico ordem = buscarPorId(id);
        ordem.setStatus("ACEITA");
        if (funcionarioId != null) {
            ordem.setFuncionario(String.valueOf(funcionarioId));
        }
        return repository.save(ordem);
    }

    public OrdemDeServico iniciar(Long id) {
        OrdemDeServico ordem = buscarPorId(id);
        ordem.setStatus("EM_ROTA");
        if (ordem.getDataInicio() == null) {
            ordem.setDataInicio(LocalDateTime.now());
        }
        return repository.save(ordem);
    }

    public OrdemDeServico finalizar(Long id, OrdemDeServico dadosFinalizacao) {
        OrdemDeServico ordem = buscarPorId(id);
        ordem.setStatus("FINALIZADA");
        ordem.setDataFinalizacao(LocalDateTime.now());

        if (dadosFinalizacao != null) {
            if (dadosFinalizacao.getProdutoAplicado() != null) {
                ordem.setProdutoAplicado(dadosFinalizacao.getProdutoAplicado());
            }
            if (dadosFinalizacao.getObservacaoTecnica() != null) {
                ordem.setObservacaoTecnica(dadosFinalizacao.getObservacaoTecnica());
            }
            if (dadosFinalizacao.getDescricao() != null && !dadosFinalizacao.getDescricao().isBlank()) {
                ordem.setDescricao(dadosFinalizacao.getDescricao());
            }
            if (dadosFinalizacao.getStringFotoBase64() != null) {
                ordem.setStringFotoBase64(dadosFinalizacao.getStringFotoBase64());
            }
        }

        return repository.save(ordem);
    }

    public OrdemDeServico atualizarGps(Long id, Double latitude, Double longitude) {
        OrdemDeServico ordem = buscarPorId(id);
        ordem.setLatitude(latitude);
        ordem.setLongitude(longitude);
        return repository.save(ordem);
    }

    public OrdemDeServico atualizar(Long id, OrdemDeServico dadosNovos) {
        OrdemDeServico existente = buscarPorId(id);
        existente.setStatus(dadosNovos.getStatus());
        existente.setData(dadosNovos.getData());
        existente.setFuncionario(dadosNovos.getFuncionario());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}