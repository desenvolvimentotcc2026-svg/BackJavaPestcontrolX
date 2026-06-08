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
                .orElseThrow(() -> new RuntimeException("Ordem não encontrada: " + id));
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
        if (ordem.getStatus() == null) {
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

    public OrdemDeServico finalizar(Long id, OrdemDeServico dados) {
        OrdemDeServico ordem = buscarPorId(id);

        ordem.setStatus("FINALIZADA");
        ordem.setDataFinalizacao(LocalDateTime.now());

        if (dados != null) {
            if (dados.getProdutoAplicado() != null)
                ordem.setProdutoAplicado(dados.getProdutoAplicado());

            if (dados.getObservacaoTecnica() != null)
                ordem.setObservacaoTecnica(dados.getObservacaoTecnica());

            if (dados.getDescricao() != null)
                ordem.setDescricao(dados.getDescricao());

            if (dados.getStringFotoBase64() != null)
                ordem.setStringFotoBase64(dados.getStringFotoBase64());
        }

        return repository.save(ordem);
    }

    public OrdemDeServico atualizarGps(Long id, Double lat, Double lng) {
        OrdemDeServico ordem = buscarPorId(id);

        ordem.setLatitude(lat);
        ordem.setLongitude(lng);

        return repository.save(ordem);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}