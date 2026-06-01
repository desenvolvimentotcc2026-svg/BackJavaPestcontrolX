package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private MensagemRepository mensagemRepository;

    public Mensagem salvarMensagem(Mensagem mensagem) {
        return mensagemRepository.save(mensagem);
    }
}