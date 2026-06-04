package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final MensagemRepository mensagemRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MensagemRepository mensagemRepository, SimpMessagingTemplate messagingTemplate) {
        this.mensagemRepository = mensagemRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{empresaId}/{clienteId}")
    public void rotearMensagem(@DestinationVariable Long empresaId, @DestinationVariable Long clienteId, Mensagem mensagem) {
        String topicoCanal = "/topic/chat/" + empresaId + "/" + clienteId;

        if (mensagem.getConteudo() != null && mensagem.getConteudo().equalsIgnoreCase("[START_BOT]")) {
            dispararRespostaBot(empresaId, clienteId, obterMenuPrincipal());
            return;
        }

        if (mensagem.getConteudo() != null && mensagem.getConteudo().equalsIgnoreCase("[ACEITOU_TERMOS]")) {
            mensagem.setDataEnvio(LocalDateTime.now());
            mensagemRepository.save(mensagem);
            return;
        }

        // Processamento de opções do Menu do Bot
        String input = mensagem.getConteudo() != null ? mensagem.getConteudo().trim() : "";

        switch (input) {
            case "1":
                dispararRespostaBot(empresaId, clienteId, obterInstitucional());
                break;
            case "2":
                dispararRespostaBot(empresaId, clienteId, obterCatalogoPragas());
                break;
            case "3":
                dispararRespostaBot(empresaId, clienteId, obterRastreamentoGPS());
                break;
            case "4":
                // Dispara o comando invisível interceptado nativamente pelo Android
                dispararRespostaBot(empresaId, clienteId, "[ABRIR_FORMULARIO_NATIVO]");
                break;
            default:
                // Tráfego normal entre Humanos (Cliente/Técnico) - persiste e encaminha
                mensagem.setDataEnvio(LocalDateTime.now());
                Mensagem salva = mensagemRepository.save(mensagem);
                messagingTemplate.convertAndSend(topicoCanal, salva);
                break;
        }
    }

    private void dispararRespostaBot(Long empresaId, Long clienteId, String textoBot) {
        Mensagem respostaBot = new Mensagem();
        respostaBot.setEmpresaId(empresaId);
        respostaBot.setClienteId(clienteId);
        respostaBot.setConteudo(textoBot);
        respostaBot.setTipoRemetente("BOT");
        respostaBot.setRemetenteId(0L);
        respostaBot.setDataEnvio(LocalDateTime.now());

        mensagemRepository.save(respostaBot);
        messagingTemplate.convertAndSend("/topic/chat/" + empresaId + "/" + clienteId, respostaBot);
    }

    private String obterMenuPrincipal() {
        return "🤖 **PAINEL CENTRAL PESTBOT**\n\n" +
                "Selecione o canal de transmissão digitando apenas o **número** da opção:\n\n" +
                "🔹 **1** - Credenciais Corporativas (Institucional)\n" +
                "🔹 **2** - Catálogo Químico e Pragas Alvo\n" +
                "🔹 **3** - Link de Telemetria GPS do Técnico\n" +
                "🔹 **4** - **Abrir Formulário de Ordem de Serviço NATIVO**";
    }

    private String obterInstitucional() {
        return "🏢 **PESTCONTROLX TECH CO.**\n\n" +
                "Líder em manejo ecológico integrado de vetores biológicos urbanos.\n" +
                "• **Licença Sanitária:** Ativa via ANVISA\n" +
                "• **IBAMA:** Registro ativo para controle e manejo seguro de impacto ambiental.";
    }

    private String obterCatalogoPragas() {
        return "☣️ **CATÁLOGO DE DEFESA BIOLÓGICA**\n\n" +
                "Táticas de choque químico disponíveis para:\n" +
                "• *Blatella germanica* (Baratas de Esgoto)\n" +
                "• *Rattus norvegicus* (Roedores / Desratização Estática)\n" +
                "• *Tityus serrulatus* (Escorpiões / Barreiras Químicas)";
    }

    private String obterRastreamentoGPS() {
        return "🛰️ **MÓDULO DE TELEMETRIA GPS**\n\n" +
                "Conexão de satélite pronta para pareamento. Assim que o técnico iniciar a rota no painel dele, o radar do seu app notificará o deslocamento.";
    }

    @PostMapping("/api/chat/trigger-acceptance")
    public ResponseEntity<Void> dispararAceitacaoOrdemRealTime(@RequestParam Long clienteId, @RequestParam Long ordemId) {
        Long empresaIdPadrao = 42L;

        Mensagem redirectMsg = new Mensagem();
        redirectMsg.setEmpresaId(empresaIdPadrao);
        redirectMsg.setClienteId(clienteId);
        redirectMsg.setConteudo("[REDIRECT_DASHBOARD]");
        redirectMsg.setTipoRemetente("SYSTEM");
        redirectMsg.setDataEnvio(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/chat/" + empresaIdPadrao + "/" + clienteId, redirectMsg);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/historico/{empresaId}/{clienteId}")
    public List<Mensagem> obterHistoricoChat(@PathVariable Long empresaId, @PathVariable Long clienteId) {
        return mensagemRepository.findByEmpresaIdAndClienteIdOrderByDataEnvioAsc(empresaId, clienteId);
    }
}