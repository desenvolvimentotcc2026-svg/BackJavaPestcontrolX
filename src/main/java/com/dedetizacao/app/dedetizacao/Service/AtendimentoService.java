package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.Atendimento;
import com.dedetizacao.app.dedetizacao.Repository.AtendimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtendimentoService {

    private final AtendimentoRepository repository;

    public AtendimentoService(AtendimentoRepository repository) {
        this.repository = repository;
    }

    public List<Atendimento> listar() {
        return repository.findAll();
    }

    public Atendimento buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Atendimento salvar(Atendimento atendimento) {
        return repository.save(atendimento);
    }

    public Atendimento atualizar(Long id, Atendimento atendimento) {
        Atendimento existente = buscarPorId(id);

        existente.setCliente(atendimento.getCliente());
        existente.setFuncionario(atendimento.getFuncionario());
        existente.setData(atendimento.getData());
        existente.setAreaTratada(atendimento.getAreaTratada());
        existente.setReclamacao(atendimento.getReclamacao());

        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}