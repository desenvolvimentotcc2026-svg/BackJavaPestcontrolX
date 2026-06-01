package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Model.Usuario;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import com.dedetizacao.app.dedetizacao.Repository.UsuarioRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public MensagemService(MensagemRepository mensagemRepository,
                           UsuarioRepository usuarioRepository,
                           EmpresaRepository empresaRepository) {
        this.mensagemRepository = mensagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<Mensagem> obterHistorico(Long user1, Long user2) {
        return mensagemRepository.buscarHistoricoConversa(user1, user2);
    }

    public Mensagem enviarMensagem(Mensagem mensagem) {
        // Define o horário de envio atual
        mensagem.setDataHora(LocalDateTime.now());
        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        // 1. Verifica quantas mensagens o remetente já mandou para o destinatário
        long quantidadeMensagensEnviadas = mensagemRepository.countByRemetenteIdAndDestinatarioId(
                mensagem.getRemetenteId(), mensagem.getDestinatarioId()
        );

        // Se for igual a 1, significa que é a PRIMEIRA mensagem desse cliente para este destinatário
        if (quantidadeMensagensEnviadas == 1) {
            // 2. Descobre o e-mail do destinatário para ver se ele é uma Empresa
            Optional<Usuario> destinoOpt = usuarioRepository.findById(mensagem.getDestinatarioId());

            if (destinoOpt.isPresent() && "EMPRESA".equals(destinoOpt.get().getTipo().name())) {
                String emailEmpresa = destinoOpt.get().getEmail();
                Optional<Empresa> empresaOpt = empresaRepository.findByEmail(emailEmpresa);

                // 3. Se a empresa existir e tiver uma mensagem automática configurada, o robô responde!
                if (empresaOpt.isPresent() && empresaOpt.get().getMensagemAutomatica() != null
                        && !empresaOpt.get().getMensagemAutomatica().trim().isEmpty()) {

                    Mensagem respostaBot = new Mensagem();
                    respostaBot.setRemetenteId(mensagem.getDestinatarioId()); // A empresa responde
                    respostaBot.setDestinatarioId(mensagem.getRemetenteId()); // Para o cliente
                    respostaBot.setConteudo(empresaOpt.get().getMensagemAutomatica());
                    respostaBot.setDataHora(LocalDateTime.now().plusSeconds(1)); // 1 segundo depois

                    mensagemRepository.save(respostaBot);
                }
            }
        }

        return reinforcementDelay(mensagemSalva);
    }

    // Pequeno delay visual caso necessário para simular digitação
    private Mensagem reinforcementDelay(Mensagem msg) {
        return msg;
    }
}