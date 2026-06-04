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

    /**
     * ENDPOINT DE HANDOVER - REST API
     * Alterado para /api/chat/... para evitar conflito com o NotificationController
     */
    @PostMapping("/api/chat/trigger-acceptance")
    public ResponseEntity<Void> dispararAceitacaoOrdemRealTime(
            @RequestParam Long clienteId,
            @RequestParam Long ordemId) {

        Long empresaIdPadrao = 42L;

        // 1. Prepara e envia o comando de redirecionamento invisível para o Cliente via WebSocket
        Mensagem redirectMsg = new Mensagem();
        redirectMsg.setEmpresaId(empresaIdPadrao);
        redirectMsg.setClienteId(clienteId);
        redirectMsg.setRemetenteId(null);
        redirectMsg.setDestinatarioId(clienteId);
        redirectMsg.setConteudo("[REDIRECT_TO_DASHBOARD]");
        redirectMsg.setEnviadoPor("SISTEMA");
        redirectMsg.setDataHora(LocalDateTime.now());

        mensagemRepository.save(redirectMsg);

        String topicoCanalCliente = "/topic/chat/" + empresaIdPadrao + "/" + clienteId;
        messagingTemplate.convertAndSend(topicoCanalCliente, redirectMsg);

        // 2. Prepara e dispara o alerta imediato no canal do Técnico associado
        Mensagem tecnicoMsg = new Mensagem();
        tecnicoMsg.setEmpresaId(empresaIdPadrao);
        tecnicoMsg.setClienteId(clienteId);
        tecnicoMsg.setConteudo("[NOVA_ORDEM_ATRIBUIDA]:" + ordemId);
        tecnicoMsg.setEnviadoPor("SISTEMA");
        tecnicoMsg.setDataHora(LocalDateTime.now());

        String topicoCanalTecnico = "/topic/tecnico/99";
        messagingTemplate.convertAndSend(topicoCanalTecnico, tecnicoMsg);

        return ResponseEntity.ok().build();
    }

    /**
     * ROTEADOR WEBSOCKET (STOMP)
     */
    @MessageMapping("/chat/{empresaId}/{clienteId}")
    public void rotearMensagem(@DestinationVariable Long empresaId, @DestinationVariable Long clienteId, Mensagem mensagem) {

        if (mensagem.getConteudo() != null && mensagem.getConteudo().equals("[START_BOT]")) {
            dispararRespostaBot(empresaId, clienteId,
                    "⚡ **SISTEMA PESTCONTROLX ACCESSED**\n\n" +
                            "Bem-vindo ao canal cibernético de triagem automatizada!\n" +
                            "Para iniciar o suporte e rotear sua Ordem de Serviço, você precisa ler e aceitar nossos Termos de Monitoramento Biológico e LGPD.\n\n" +
                            "⚠️ *Por favor, marque a caixa de seleção abaixo para liberar o painel.*");
            return;
        }

        if (mensagem.getConteudo() != null && mensagem.getConteudo().equals("[ACEITOU_TERMOS]")) {
            dispararRespostaBot(empresaId, clienteId,
                    "✅ **TERMOS ACEITOS COM SUCESSO!**\n\n" +
                            "Painel destravado. Digite **MENU** a qualquer momento para ver nossas opções de defesa.");
            return;
        }

        mensagem.setEmpresaId(empresaId);
        mensagem.setClienteId(clienteId);
        mensagem.setDataHora(LocalDateTime.now());
        mensagemRepository.save(mensagem);

        if (mensagem.getConteudo() == null) return;
        String textoUsuario = mensagem.getConteudo().trim().toUpperCase();

        if (textoUsuario.equals("MENU")) {
            dispararRespostaBot(empresaId, clienteId, obterMenuPrincipal());
        } else if (textoUsuario.equals("1")) {
            dispararRespostaBot(empresaId, clienteId, obterInstitucional());
        } else if (textoUsuario.equals("2")) {
            dispararRespostaBot(empresaId, clienteId, obterCatalogoPragas());
        } else if (textoUsuario.equals("3")) {
            dispararRespostaBot(empresaId, clienteId, obterRastreamentoGPS());
        } else if (textoUsuario.equals("4")) {
            String linkOrdem = "https://pestcontrolx-web.onrender.com/form-ordem.html?clienteId=" + clienteId + "&empresaId=" + empresaId;
            dispararRespostaBot(empresaId, clienteId,
                    "📝 **DIRECIONAMENTO - FORMULÁRIO DE ORDEM DE SERVIÇO**\n\n" +
                            "Para gerar e formalizar sua O.S. no ecossistema digital, clique no link oficial abaixo para preencher os dados do foco de pragas:\n\n" +
                            "🔗 " + linkOrdem + "\n\n" +
                            "Após o preenchimento, o console do Técnico de Campo será alertado em tempo real!");
        } else {
            messagingTemplate.convertAndSend("/topic/chat/" + empresaId + "/" + clienteId, mensagem);
        }
    }

    /**
     * RECUPERAÇÃO DE HISTÓRICO - REST API
     */
    @GetMapping("/api/chat/historico/{empresaId}/{clienteId}")
    public List<Mensagem> buscarHistorico(@PathVariable Long empresaId, @PathVariable Long clienteId) {
        return mensagemRepository.findTop50ByEmpresaIdAndClienteIdOrderByDataHoraAsc(empresaId, clienteId);
    }

    private void dispararRespostaBot(Long empresaId, Long clienteId, String textoBot) {
        Mensagem botMsg = new Mensagem();
        botMsg.setEmpresaId(empresaId);
        botMsg.setClienteId(clienteId);
        botMsg.setRemetenteId(null);
        botMsg.setDestinatarioId(clienteId);
        botMsg.setConteudo(textoBot);
        botMsg.setEnviadoPor("BOT");
        botMsg.setDataHora(LocalDateTime.now());

        mensagemRepository.save(botMsg);
        messagingTemplate.convertAndSend("/topic/chat/" + empresaId + "/" + clienteId, botMsg);
    }

    private String obterMenuPrincipal() {
        return "🤖 **PAINEL CENTRAL PESTBOT**\n\n" +
                "Selecione o canal de transmissão digitando apenas o **número** da opção:\n\n" +
                "🔹 **1** - Credenciais Corporativas (Institucional)\n" +
                "🔹 **2** - Catálogo Químico e Pragas Alvo\n" +
                "🔹 **3** - Link de Telemetria GPS do Técnico\n" +
                "🔹 **4** - **Formar Nova Ordem de Serviço (WEB)**";
    }

    private String obterInstitucional() {
        return "🏢 **PESTCONTROLX TECH CO.**\n\n" +
                "Líder em manejo ecológico integrado de vetores biológicos urbanos.\n" +
                "• **Licença Sanitária:** Ativa via ANVISA\n" +
                "• **IBAMA:** Registro ativo para controle e manejo seguro de impacto ambientall.";
    }

    private String obterCatalogoPragas() {
        return "☣️ **CATÁLOGO DE DEFESA BIOLÓGICA**\n\n" +
                "Táticas de choque químico disponíveis para:\\n" +
                "• *Blatella germanica* (Baratas de Esgoto)\n" +
                "• *Rattus norvegicus* (Roedores / Desratização Estática)\n" +
                "• *Tityus serrulatus* (Escorpiões / Barreiras Químicas)";
    }

    private String obterRastreamentoGPS() {
        return "🛰️ **MÓDULO DE TELEMETRIA GPS**\n\n" +
                "Conexão de satélite pronta para pareamento. Assim que o técnico iniciar a rota no painel dele, o radar do seu app notificará o deslocamento.";
    }
}