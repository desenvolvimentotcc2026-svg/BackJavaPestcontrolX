package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.SolicitacaoRepository;

import com.dedetizacao.app.dedetizacao.Service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private NotificationService notificationService;

    // Simula o banco de tokens das Contas Comerciais das Empresas cadastradas
    private static final String TOKEN_CONTA_COMERCIAL_EMPRESA = "TOKEN_FCM_DA_EMPRESA_AQUI";

    @PostMapping("/assinar-termo")
    public ResponseEntity<Map<String, Object>> assinarTermoEConverter(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam Long empresaId,
            @RequestParam String descricaoInfestacao) {

        // 1. 🚀 SALVA E REGISTRA O NOVO CLIENTE DO ONBOARDING
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setEmail(email);
        novoCliente.setTelefone(telefone);
        // Se a sua model de Cliente possuir o campo empresaId ou relacionamento:
        // novoCliente.setEmpresaId(empresaId);

        Cliente clienteSalvo = clienteRepository.save(novoCliente);

        // 2. 📝 GERA A ORDEM DE SERVIÇO VINCULADA
        OrdemDeServico os = new OrdemDeServico();
        os.setCliente(clienteSalvo); // Passa o cliente persistido com ID gerado
        os.setStatus("PENDENTE");
        os.setDescricao("Contrato Assinado Digitalmente via Chatbot - " + descricaoInfestacao);
        os.setDataAgendamento(LocalDateTime.now().plusDays(2).toString()); // Agenda para daqui a 2 dias

        OrdemDeServico osSalva = solicitacaoRepository.save(os);

        // 3. 📢 DISPARA A NOTIFICAÇÃO REAL-TIME PARA A EMPRESA
        String tituloPush = "🚨 Nova OS Gerada via Chatbot!";
        String corpoPush = "O cliente " + nome + " acabou de aceitar os termos. OS N° " + osSalva.getId() + " aguardando aprovação.";

        try {
            notificationService.sendPushNotification(TOKEN_CONTA_COMERCIAL_EMPRESA, tituloPush, corpoPush);
            System.out.println("🟢 Notificação comercial despachada com sucesso para a empresa ID: " + empresaId);
        } catch (Exception e) {
            System.err.println("⚠️ Falha ao enviar Push FCM para a empresa: " + e.getMessage());
        }

        // 4. ✨ RETORNA O PAYLOAD DE SUCESSO DE FORMA LIMPA
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", "SUCESSO");
        resposta.put("mensagem", "Termo de consciência assinado e empresa notificada!");
        resposta.put("clienteId", clienteSalvo.getId());
        resposta.put("ordemId", osSalva.getId());

        return ResponseEntity.ok(resposta);
    }
}