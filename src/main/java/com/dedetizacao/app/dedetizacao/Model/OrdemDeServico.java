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

    @Column(length = 2000)
    private String descricao;

    private String status;
    private String funcionario;
    private String dataAgendamento;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinalizacao;
    private Double latitude;
    private Double longitude;
    private String produtoAplicado;

    @Column(length = 2000)
    private String observacaoTecnica;

    @Column(columnDefinition = "TEXT")
    private String stringFotoBase64;

    public OrdemDeServico() {
    }

    public OrdemDeServico(String cliente, String descricao) {
        this.cliente = cliente;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public void setCliente(Cliente clienteObj) {
        if (clienteObj != null) {
            this.cliente = clienteObj.getNome();
            this.clienteId = clienteObj.getId();
        }
    }

    public String getPragaAlvo() { return pragaAlvo; }
    public void setPragaAlvo(String pragaAlvo) { this.pragaAlvo = pragaAlvo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFuncionario() { return funcionario; }
    public void setFuncionario(String funcionario) { this.funcionario = funcionario; }

    public String getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(String dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(LocalDateTime dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getProdutoAplicado() { return produtoAplicado; }
    public void setProdutoAplicado(String produtoAplicado) { this.produtoAplicado = produtoAplicado; }

    public String getObservacaoTecnica() { return observacaoTecnica; }
    public void setObservacaoTecnica(String observacaoTecnica) { this.observacaoTecnica = observacaoTecnica; }

    public String getStringFotoBase64() { return stringFotoBase64; }
    public void setStringFotoBase64(String stringFotoBase64) { this.stringFotoBase64 = stringFotoBase64; }

    public LocalDateTime getData() { return this.dataAbertura != null ? this.dataAbertura : LocalDateTime.now(); }
    public void setData(LocalDateTime data) { this.dataAbertura = data; }

    public String getEmpresa() { return this.empresaId != null ? String.valueOf(this.empresaId) : ""; }

    public void setEmpresa(Empresa empresaObj) {
        if (empresaObj != null) {
            this.empresaId = empresaObj.getId();
        }
    }
}