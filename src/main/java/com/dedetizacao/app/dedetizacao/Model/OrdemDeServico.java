package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ordem_servico")
public class OrdemDeServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "empresa_id") private Empresa empresa;
    @ManyToOne @JoinColumn(name = "cliente_id") private Cliente cliente;
    @ManyToOne @JoinColumn(name = "funcionario_id") private Funcionario funcionario;
    @ManyToOne @JoinColumn(name = "servico_id") private Servico servico;

    private String status;
    private LocalDate data;
    private String dataAgendamento;
    // Verifique se o campo existe no topo da classe
    private String descricao;

    // Adicione este método dentro da classe
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Já que você está aí, certifique-se que o getter também existe:
    public String getDescricao() {
        return this.descricao;
    }

    // Construtores
    public OrdemDeServico() {}

    // GETTERS E SETTERS CRUCIAIS PARA O BUILD
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(String dataAgendamento) { this.dataAgendamento = dataAgendamento; }
}