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

        // Gatilho Oculto: Abertura da Tela
        if (textoLimpo.equals("[START_BOT]")) {
            dispararRespostaBot(empresaId, clienteId, obterMenuPrincipal());
            return;
        }

        // Salva a mensagem do humano
        mensagem.setEmpresaId(empresaId);
        mensagem.setClienteId(clienteId);
        mensagem.setDataHora(LocalDateTime.now());
        mensagem.setTipoRemetente("Humano");

        Mensagem mensagemSalva = mensagemRepository.save(mensagem);
        messagingTemplate.convertAndSend(topicoCanal, mensagemSalva);

        // IA Básica / Máquina de Estados do PestBot
        processarComandoBot(textoLimpo, empresaId, clienteId);
    }

    private void processarComandoBot(String comando, Long empresaId, Long clienteId) {
        comando = comando.toLowerCase();

        switch (comando) {
            case "menu":
            case "ajuda":
            case "bot":
                dispararRespostaBot(empresaId, clienteId, obterMenuPrincipal());
                break;
            case "1":
                dispararRespostaBot(empresaId, clienteId, obterTermosLGPD());
                break;
            case "2":
                dispararRespostaBot(empresaId, clienteId, obterLicenciamentoAmbiental());
                break;
            case "3":
                dispararRespostaBot(empresaId, clienteId, obterCatalogoPragas());
                break;
            case "4":
                dispararRespostaBot(empresaId, clienteId, obterRastreamentoGPS());
                break;
            case "5":
                dispararRespostaBot(empresaId, clienteId, obterSuporteHumano());
                break;
            default:
                // Resposta inteligente para comandos não reconhecidos
                if(comando.length() == 1) {
                    dispararRespostaBot(empresaId, clienteId, "⚠️ **ERRO DE SINTAXE**\nComando '" + comando + "' não reconhecido pelo terminal. Digite **MENU** para reiniciar a interface de suporte.");
                }
                break;
        }
    }

    private void dispararRespostaBot(Long empresaId, Long clienteId, String conteudoBot) {
        Mensagem respostaBot = new Mensagem();
        respostaBot.setEmpresaId(empresaId);
        respostaBot.setClienteId(clienteId);
        respostaBot.setRemetenteId(empresaId);
        respostaBot.setDestinatarioId(clienteId);
        respostaBot.setConteudo(conteudoBot);
        respostaBot.setDataHora(LocalDateTime.now());
        respostaBot.setEnviadoPor("BOT");
        respostaBot.setTipoRemetente("BOT");

        try {
            mensagemRepository.save(respostaBot);
        } catch (Exception e) {
            System.err.println("WARN: Falha ao persistir log do bot -> " + e.getMessage());
        }

        String topicoCanal = "/topic/chat/" + empresaId + "/" + clienteId;
        messagingTemplate.convertAndSend(topicoCanal, respostaBot);
    }

    // ---------------- TEXTOS DO BOT ---------------- //

    private String obterMenuPrincipal() {
        return "⚡ **TERMINAL PESTBOT | SISTEMA OPERACIONAL** ⚡\n\n" +
                "Protocolo de atendimento ativado. Sou seu assistente virtual de triagem. " +
                "Para prosseguir com o seu monitoramento, insira o **NÚMERO** da diretriz desejada:\n\n" +
                "➔ **[1]** 🔐 Termos Jurídicos e Consentimento LGPD\n" +
                "➔ **[2]** 🧪 Licenciamento Técnico (ANVISA/IBAMA)\n" +
                "➔ **[3]** ☣️ Catálogo de Defesa Biológica e Pragas\n" +
                "➔ **[4]** 🛰️ Sincronizar Rastreamento GPS do Técnico\n" +
                "➔ **[5]** 👨‍🔧 Bypass: Conectar Operador Humano";
    }

    private String obterTermosLGPD() {
        return "🔐 **DIRETRIZES DE PRIVACIDADE E DADOS (LGPD)**\n\n" +
                "A plataforma PestControlX opera sob criptografia avançada. Os logs de interação, " +
                "telemetria GPS e dados estruturais são retidos unicamente para a emissão de " +
                "Certificados de Desinfecção Obrigatórios.\n\n" +
                "*Status: Protocolo de segurança operando em conformidade com a ANPD.*";
    }

    private String obterLicenciamentoAmbiental() {
        return "🧪 **SISTEMA DE CONFORMIDADE E LICENCIAMENTO**\n\n" +
                "Nossas operações químicas são auditadas sob os seguintes registros:\n" +
                "• **ANVISA:** Uso de domissanitários de ação focal com baixíssima toxicidade para mamíferos.\n" +
                "• **IBAMA:** Registro ativo para controle e manejo seguro de impacto ambiental.";
    }

    private String obterCatalogoPragas() {
        return "☣️ **CATÁLOGO DE DEFESA BIOLÓGICA**\n\n" +
                "Táticas de choque químico e manejo integrados disponíveis para:\n" +
                "• Blatella germanica (Baratas)\n" +
                "• Rattus norvegicus (Roedores)\n" +
                "• Tityus serrulatus (Escorpiões)\n" +
                "• Coptotermes gestroi (Cupins Subterrâneos)";
    }

    private String obterRastreamentoGPS() {
        return "🛰️ **MÓDULO DE TELEMETRIA GPS**\n\n" +
                "Conexão de satélite pronta para pareamento. Assim que o técnico iniciar a rota, " +
                "o radar no seu dashboard será ativado em tempo real.\n" +
                "*(Certifique-se de manter as permissões de localização do Android ativas).*";
    }

    private String obterSuporteHumano() {
        return "👨‍🔧 **BYPASS AUTORIZADO | TRANSFERÊNCIA**\n\n" +
                "Ping de prioridade enviado ao dashboard corporativo. " +
                "Um especialista técnico vai assumir a comunicação nesta mesma interface em instantes. Aguarde na linha... 🟢";
    }

    @GetMapping("/api/chat/historico/{empresaId}/{clienteId}")
    public List<Mensagem> buscarHistorico(@PathVariable Long empresaId, @PathVariable Long clienteId) {
        return mensagemRepository.findByEmpresaIdAndClienteId(empresaId, clienteId);
    }
}