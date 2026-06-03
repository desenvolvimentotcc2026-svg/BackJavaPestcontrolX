package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final MensagemRepository repository;

    public ChatService(MensagemRepository repository) {
        this.repository = repository;
    }

    public List<Mensagem> buscarHistorico(Long empresaId, Long clienteId) {
        return repository.findByEmpresaIdAndClienteId(empresaId, clienteId);
    }
}