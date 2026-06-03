package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    @SendTo("/topic/chat/{empresaId}/{clienteId}")
    public Mensagem rotearMensagem(@DestinationVariable Long empresaId, @DestinationVariable Long clienteId, Mensagem mensagem) {
        mensagem.setEmpresaId(empresaId);
        mensagem.setClienteId(clienteId);
        mensagem.setDataHora(LocalDateTime.now());
        mensagem.setTipoRemetente("Humano"); // Marca a msg que acabou de chegar como humana

        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        String textoLimpo = mensagem.getConteudo() != null ? mensagem.getConteudo().trim() : "";

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

        return mensagemSalva;
    }

    private void dispararRespostaBot(Long empresaId, Long clienteId, String conteudoBot, Long usuarioClienteId) {
        Mensagem respostaBot = new Mensagem();
        respostaBot.setEmpresaId(empresaId);
        respostaBot.setClienteId(clienteId);
        
        respostaBot.setRemetenteId(empresaId);
        respostaBot.setDestinatarioId(clienteId);

        respostaBot.setConteudo(conteudoBot);
        respostaBot.setDataHora(LocalDateTime.now());
        respostaBot.setTipoRemetente("PestBot");

        try {
            mensagemRepository.save(respostaBot); // Agora o banco vai aceitar!
        } catch (Exception e) {
            System.err.println("Erro ao salvar mensagem do Bot (Ignorando para não travar o socket): " + e.getMessage());
        }

        String topicoCanal = "/topic/chat/" + empresaId + "/" + clienteId;
        messagingTemplate.convertAndSend(topicoCanal, respostaBot);
        System.out.println("--> [PESTBOT] Resposta automática injetada com sucesso no canal: " + topicoCanal);
    }

    private String obterMenuPrincipal() {
        return "🤖 **[PestBot - SUPORTE OPERACIONAL VIRTUAL]**\n\n" +
                "Olá! Sou o PestBot, o assistente digital integrado do PestControlX.\n" +
                "Para acelerar o andamento da sua ordem de serviço e garantir nossa conformidade regulatória, " +
                "digite apenas o **NÚMERO** correspondente à opção desejada:\n\n" +
                "[1] 📜 Termos de Consentimento Jurídico e LGPD\n" +
                "[2] 🪪 Certificação Ambiental e Alvará Sanitário (Anvisa/Ibama)\n" +
                "[3] 🎯 Catálogo Técnico de Pragas Urbanas e Vetores\n" +
                "[4] 🛰️ Ativar Monitoramento GPS do Técnico em Tempo Real\n" +
                "[5] 👨‍🔧 Transferir para Atendimento Humano (Falar com Especialista)";
    }

    private String obterTermosLGPD() {
        return "📜 **TERMOS DE CONSENTIMENTO E DIRETRIZES LEGAIS - LGPD (Lei nº 13.709/18)**\n\n" +
                "Para fins de segurança civil e eficácia operacional, a plataforma PestControlX realiza o tratamento " +
                "e armazenamento de dados cadastrais, logs de interação interativa de chat e telemetria baseada em geolocalização por satélite.\n\n" +
                "Estes registros são protegidos sob criptografia relacional em nosso banco de dados, sendo utilizados " +
                "estritamente para a emissão de Certificados de Desinfecção Obrigatórios e relatórios exigidos pela vigilância sanitária. " +
                "Ao manter o canal ativo, o usuário declara consentimento inequívoco quanto à coleta e tratamento seguro de dados conforme os padrões da ANPD.";
    }

    private String obterLicenciamentoAmbiental() {
        return "🪪 **CONFORMIDADE TÉCNICA E LICENCIAMENTO AMBIENTAL**\n\n" +
                "A empresa prestadora opera sob homologação técnica estrita nos seguintes marcos regulatórios:\n\n" +
                "• **ANVISA (RDC nº 52/2009):** Funcionamento integralmente autorizado para o controle de vetores e pragas utilizando domissanitários de uso restrito profissional com baixíssima toxicidade residual.\n" +
                "• **IBAMA (Instrução Normativa nº 13/2013):** Registro ativo no Cadastro Técnico Federal (CTF) para atividades potencialmente poluidoras.\n" +
                "• **CONSELHO DE CLASSE:** Operações supervisionadas com emissão de ART (Anotação de Responsabilidade Técnico).";
    }

    private String obterCatalogoPragas() {
        return "🎯 **MANEJO INTEGRADO DE VETORES URBANOS**\n\n" +
                "Nosso plano operacional mapeia barreiras de choque e controle contra:\n" +
                "• *Blatella germanica* & *Periplaneta americana* (Baratas / Gel de Saturação)\n" +
                "• *Rattus norvegicus* & *Mus musculus* (Roedores / Blocos Parafinados Iscados)\n" +
                "• *Tityus serrulatus* (Escorpiões / Formulações microencapsuladas de altíssima persistência)\n" +
                "• *Coptotermes gestroi* (Cupins de Solo / Barreira Química Profunda).";
    }

    private String obterRastreamentoGPS() {
        return "🛰️ **LINK DE GEOLOCALIZAÇÃO E TELEMETRIA CORPORATIVA**\n\n" +
                "O canal de satélite local está pronto para monitorar o deslocamento do seu técnico de campo!\n\n" +
                "• **Ação Requerida:** O aplicativo PestControlX iniciará o processo de mapeamento cartográfico. " +
                "Quando o alerta nativo do Android aparecer, selecione **'Permitir durante o uso do aplicativo'**.\n" +
                "• Isso liberará a sincronização entre a rota do veículo e seu endereço físico de forma totalmente segura.";
    }

    private String obterSuporteHumano() {
        return "👨‍🔧 **DIRECIONAMENTO - CANAL HUMANO LIBERADO**\n\n" +
                "Triagem automatizada concluída com sucesso! O PestBot congelou as respostas automáticas " +
                "e enviou uma notificação push de alta prioridade para o console administrativo do Técnico de Campo.\n\n" +
                "Permaneça no chat, o especialista assumirá a digitação humana em instantes! 🟢";
    }

    @GetMapping("/api/chat/historico/{empresaId}/{clienteId}")
    public List<Mensagem> buscarHistorico(@PathVariable Long empresaId, @PathVariable Long clienteId) {
        return mensagemRepository.findByEmpresaIdAndClienteId(empresaId, clienteId);
    }
}