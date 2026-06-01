package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Atendimento;
import com.dedetizacao.app.dedetizacao.Service.AtendimentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    private final AtendimentoService service;

    public AtendimentoController(AtendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Atendimento> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Atendimento buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Atendimento criar(@RequestBody Atendimento atendimento) {
        return service.salvar(atendimento);
    }

    @PutMapping("/{id}")
    public Atendimento atualizar(@PathVariable Long id,
                                 @RequestBody Atendimento atendimento) {
        return service.atualizar(id, atendimento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}