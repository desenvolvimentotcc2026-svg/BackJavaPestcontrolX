package com.dedetizacao.app.dedetizacao.Dto;

import java.time.LocalDate;

public class ServicoDto {

    private Long id;

    private String tipoServico;

    private String descricao;

    private LocalDate dataServico;

    private Long cliente_id;

    private Long funcionarioId;

    public ServicoDto() {
    }

}