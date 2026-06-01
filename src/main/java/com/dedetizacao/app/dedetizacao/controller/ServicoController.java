package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Model.Servico;
import com.dedetizacao.app.dedetizacao.Service.ServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService){
        this.servicoService = servicoService;
    }

    @PostMapping
    public Servico criar(@RequestBody Servico servico){
        return servicoService.salvar(servico);
    }

    @GetMapping
    public List<Servico> listar(){
        return servicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Servico buscar(@PathVariable Long id){
        return servicoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Servico atualizar(@PathVariable Long id, @RequestBody Servico servico){
        return servicoService.atualizar(id, servico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}