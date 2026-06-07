package com.dedetizacao.app.dedetizacao.Dto;

import java.time.LocalDate;

public class AgendamentoDto {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getOrdemId(){
        return ordemId;
    }

    public void setOrdemId(Long ordemId){
        this.ordemId = ordemId;
    }

    private Long clienteId;

    private Long funcionarioId;

    private Long servicoId;

    private LocalDate data;

    private Long ordemId;

    public AgendamentoDto() {
    }

}
