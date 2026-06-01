package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import com.dedetizacao.app.dedetizacao.Service.MensagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
@CrossOrigin(origins = "*")
public class MensagemController {

    private final MensagemService mensagemService;

    public MensagemController(MensagemService mensagemService) {
        this.mensagemService = mensagemService;
    }

    @PostMapping
    public ResponseEntity<Mensagem> enviarMensagem(@RequestBody Mensagem mensagem) {
        Mensagem novaMensagem = mensagemService.enviarMensagem(mensagem);
        return ResponseEntity.ok(novaMensagem);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<Mensagem>> obterHistorico(
            @RequestParam("user1") Long user1,
            @RequestParam("user2") Long user2) {

        List<Mensagem> historico = mensagemService.obterHistorico(user1, user2);
        return ResponseEntity.ok(historico);
    }
}