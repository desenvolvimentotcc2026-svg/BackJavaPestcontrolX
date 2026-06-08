package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.AgendamentoDto;
import com.dedetizacao.app.dedetizacao.Model.Agendamento;
import com.dedetizacao.app.dedetizacao.Service.AgendamentoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Agendamento> criar(
            @RequestBody AgendamentoDto dto) {

        return ResponseEntity.ok(
                service.criar(dto)
        );
    }
}
