package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordens_servico")
public class OrdemDeServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private Long empresaId;
    private String cliente;
    private String pragaAlvo;
    private String descricao;
    private String status;

    private LocalDateTime dataAbertura; // Campo exigido pela linha 42 do OrdemDeServicoController

    // Construtores
    public OrdemDeServico() {
    }

    public OrdemDeServico(String cliente, String descricao) {
        this.cliente = cliente;
        this.descricao = descricao;
    }

    // Getters e Setters Estruturados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getPragaAlvo() { return pragaAlvo; }
    public void setPragaAlvo(String pragaAlvo) { this.pragaAlvo = pragaAlvo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
}