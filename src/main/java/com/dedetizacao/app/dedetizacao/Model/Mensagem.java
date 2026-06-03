package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
        String textoLimpo = mensagem.getConteudo() != null ? mensagem.getConteudo().trim() : "";
        String topicoCanal = "/topic/chat/" + empresaId + "/" + clienteId;

        // 🔥 1. O GATILHO SILENCIOSO: Se for o Android pedindo o menu inicial
        if (textoLimpo.equals("[START_BOT]")) {
            dispararRespostaBot(empresaId, clienteId, obterMenuPrincipal(), mensagem.getRemetenteId());
            return; // Interrompe aqui! A mensagem "[START_BOT]" não é salva e não aparece pro usuário.
        }

        // 2. FLUXO NORMAL: Mensagem real enviada pelo humano
        mensagem.setEmpresaId(empresaId);
        mensagem.setClienteId(clienteId);
        mensagem.setDataHora(LocalDateTime.now());

        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        // Espelha a mensagem digitada para o canal WebSocket
        messagingTemplate.convertAndSend(topicoCanal, mensagemSalva);

        // 3. REGRAS DO BOT: Analisa se o humano escolheu alguma opção
        if (textoLimpo.equalsIgnoreCase("menu") || textoLimpo.equalsIgnoreCase("ajuda") || textoLimpo.equalsIgnoreCase("bot")) {
            dispararRespostaBot(empresaId, clienteId, obterMenuPrincipal(), mensagem.getRemetenteId());
        } else if (textoLimpo.equals("1")) {
            dispararRespostaBot(empresaId, clienteId, obterTermosLGPD(), mensagem.getRemetenteId());
        } else if (textoLimpo.equals("2")) {
            dispararRespostaBot(empresaId, clienteId, obterLicenciamentoAmbiental(), mensagem.getRemetenteId());
        } else if (textoLimpo.equals("3")) {
            dispararRespostaBot(empresaId, clienteId, obterCatalogoPragas(), mensagem.getRemetenteId());
        } else if (textoLimpo.equals("4")) {
            dispararRespostaBot(empresaId, clienteId, obterRastreamentoGPS(), mensagem.getRemetenteId());
        } else if (textoLimpo.equals("5")) {
            dispararRespostaBot(empresaId, clienteId, obterSuporteHumano(), mensagem.getRemetenteId());
        }
    }

    private void dispararRespostaBot(Long empresaId, Long clienteId, String conteudoBot, Long usuarioClienteId) {
        Mensagem respostaBot = new Mensagem();
        respostaBot.setEmpresaId(empresaId);
        respostaBot.setClienteId(clienteId);
        respostaBot.setRemetenteId(empresaId);
        respostaBot.setDestinatarioId(clienteId);
        respostaBot.setConteudo(conteudoBot);
        respostaBot.setDataHora(LocalDateTime.now());

        // Padronizado para o Android ler a cor azul corretamente
        respostaBot.setEnviadoPor("BOT");

        try {
            mensagemRepository.save(respostaBot);
        } catch (Exception e) {
            System.err.println("Erro ao salvar mensagem do Bot: " + e.getMessage());
        }

        String topicoCanal = "/topic/chat/" + empresaId + "/" + clienteId;
        messagingTemplate.convertAndSend(topicoCanal, respostaBot);
    }

    private String obterMenuPrincipal() {
        return "🤖 **[PestBot - SUPORTE OPERACIONAL VIRTUAL]**\n\n" +
                "Olá! Sou o PestBot, o assistente digital integrado do PestControlX.\n" +
                "Para acelerar o andamento da sua ordem de serviço e garantir nossa conformidade regulatória, " +
                "digite apenas o **NÚMERO** correspondente à opção desejada:\n\n" +
                "[1] 📜 Termos de Consentimento Jurídico e LGPD\n" +
                "[2] 🪪 Certificação Ambiental e Alvará Sanitário\n" +
                "[3] 🎯 Catálogo Técnico de Pragas Urbanas e Vetores\n" +
                "[4] 🛰️ Ativar Monitoramento GPS do Técnico\n" +
                "[5] 👨‍🔧 Transferir para Atendimento Humano";
    }

    private String obterTermosLGPD() { return "📜 **TERMOS DE CONSENTIMENTO E DIRETRIZES LEGAIS - LGPD (Lei nº 13.709/18)**\n\nPara fins de segurança civil e eficácia operacional, a plataforma PestControlX realiza o tratamento e armazenamento de dados cadastrais, logs de interação interativa de chat e telemetria baseada em geolocalização por satélite."; }
    private String obterLicenciamentoAmbiental() { return "🪪 **CONFORMIDADE TÉCNICA E LICENCIAMENTO AMBIENTAL**\n\nA empresa prestadora opera sob homologação técnica estrita nos seguintes marcos regulatórios da ANVISA e IBAMA."; }
    private String obterCatalogoPragas() { return "🎯 **MANEJO INTEGRADO DE VETORES URBANOS**\n\nNosso plano operacional mapeia barreiras de choque e controle contra diversas pragas."; }
    private String obterRastreamentoGPS() { return "🛰️ **LINK DE GEOLOCALIZAÇÃO E TELEMETRIA CORPORATIVA**\n\nO canal de satélite local está pronto para monitorar o deslocamento do seu técnico de campo!"; }
    private String obterSuporteHumano() { return "👨‍🔧 **DIRECIONAMENTO - CANAL HUMANO LIBERADO**\n\nTriagem automatizada concluída com sucesso! Permaneça no chat, o especialista assumirá a digitação humana em instantes! 🟢"; }

    @GetMapping("/api/chat/historico/{empresaId}/{clienteId}")
    public List<Mensagem> buscarHistorico(@PathVariable Long empresaId, @PathVariable Long clienteId) {
        return mensagemRepository.findByEmpresaIdAndClienteId(empresaId, clienteId);
    }
}