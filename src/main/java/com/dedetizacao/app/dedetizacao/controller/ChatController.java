package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final MensagemRepository mensagemRepository;

    public ChatController(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    // 1. ROTA WEBSOCKET (Tempo Real)
    // Quando alguém manda para /app/chat/{empresaId}/{clienteId}
    @MessageMapping("/chat/{empresaId}/{clienteId}")
    @SendTo("/topic/chat/{empresaId}/{clienteId}")
    public Mensagem rotearMensagem(@DestinationVariable Long empresaId, @DestinationVariable Long clienteId, Mensagem mensagem) {
        mensagem.setEmpresaId(empresaId);
        mensagem.setClienteId(clienteId);
        // Salva no banco e dispara para quem estiver escutando o tópico
        return mensagemRepository.save(mensagem);
    }

    // 2. ROTA REST (Histórico)
    // Para carregar as mensagens antigas quando você clica no cliente
    @GetMapping("/api/chat/historico/{empresaId}/{clienteId}")
    public List<Mensagem> buscarHistorico(@PathVariable Long empresaId, @PathVariable Long clienteId) {
        return mensagemRepository.findByEmpresaIdAndClienteIdOrderByDataHoraAsc(empresaId, clienteId);
    }
}