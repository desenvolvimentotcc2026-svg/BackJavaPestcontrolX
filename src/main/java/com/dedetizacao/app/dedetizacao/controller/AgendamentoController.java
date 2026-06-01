package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.AgendamentoDto;
import com.dedetizacao.app.dedetizacao.Model.Agendamento;
import com.dedetizacao.app.dedetizacao.Service.AgendamentoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public Agendamento criar(@RequestBody AgendamentoDto dto) {
        return agendamentoService.criar(dto);
    }
}
