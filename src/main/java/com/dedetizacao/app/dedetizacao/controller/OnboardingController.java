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

    private static final String TOKEN_CONTA_COMERCIAL_EMPRESA = "TOKEN_FCM_DA_EMPRESA_AQUI";

    @PostMapping("/assinar-termo")
    public ResponseEntity<Map<String, Object>> assinarTermoEConverter(
            @RequestParam String nome,
            @RequestParam String telefone,
            @RequestParam String descricaoInfestacao,
            @RequestParam Long empresaId) {

        // 1. Persiste o cliente de forma limpa
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // 2. Transforma o fluxo em uma Ordem de Serviço ativa
        OrdemDeServico os = new OrdemDeServico();
        os.setCliente(clienteSalvo); // Resolvido por sobrecarga nativa
        os.setEmpresaId(empresaId);
        os.setStatus("PENDENTE");
        os.setDescricao("Contrato Assinado Digitalmente via Chatbot - " + descricaoInfestacao);
        os.setDataAgendamento(LocalDateTime.now().plusDays(2).toString());
        os.setDataAbertura(LocalDateTime.now());

        OrdemDeServico osSalva = solicitacaoRepository.save(os);

        // 3. Comunicação em tempo real com o cluster corporativo
        String tituloPush = "🚨 Nova OS Gerada via Chatbot!";
        String corpoPush = "O cliente " + nome + " acabou de aceitar os termos. OS N° " + osSalva.getId() + " aguardando aprovação.";

        try {
            notificationService.sendPushNotification(TOKEN_CONTA_COMERCIAL_EMPRESA, tituloPush, corpoPush);
            System.out.println("🟢 Notificação comercial despachada com sucesso para a empresa ID: " + empresaId);
        } catch (Exception e) {
            System.err.println("⚠️ Falha ao enviar Push FCM para a empresa: " + e.getMessage());
        }

        // 4. Retorno limpo estruturado
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", "SUCESSO");
        resposta.put("mensagem", "Termo de consciência assinado e OS criada com sucesso.");
        resposta.put("ordemId", osSalva.getId());

        return ResponseEntity.ok(resposta);
    }
}