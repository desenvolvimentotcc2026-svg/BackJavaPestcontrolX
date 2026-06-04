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

    // Atributos de compatibilidade integrados de forma segura
    private String funcionario;
    private String dataAgendamento;
    private LocalDateTime dataAbertura;

    // Construtores
    public OrdemDeServico() {
    }

    public OrdemDeServico(String cliente, String descricao) {
        this.cliente = cliente;
        this.descricao = descricao;
    }

    // Getters e Setters Padrão
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

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getData() { return this.dataAbertura != null ? this.dataAbertura : LocalDateTime.now(); }
    public void setData(LocalDateTime data) { this.dataAbertura = data; }

    public String getFuncionario() { return funcionario; }
    public void setFuncionario(String funcionario) { this.funcionario = funcionario; }

    public String getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(String dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    // Retorna representação em string segura para evitar quebra no OrdemDeServicoController
    public String getEmpresa() { return this.empresaId != null ? String.valueOf(this.empresaId) : ""; }

    // Aceita objeto Empresa vindo de controllers legados (ex: SolicitacaoController)
    public void setEmpresa(Empresa empresaObj) {
        if (empresaObj != null) {
            this.empresaId = empresaObj.getId();
        }
    }
}