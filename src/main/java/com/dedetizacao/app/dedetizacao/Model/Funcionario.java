package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;
    private Boolean ativo = true;
    private String cargo;

    @Column(nullable = false, unique = true)
    private String cpf;
    private String senha;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String telefone;

    @ManyToOne
    @JoinColumn(name = "empresa_id") // Define o nome da coluna de Chave Estrangeira (FK) no banco.
    private Empresa empresa;

    @OneToMany(mappedBy = "funcionario") // Lado inverso da relação; o controle de dados fica na classe Servico.
    @JsonIgnore // Impede que a Api trave em um loop infinito ao converter os dados para JSON.
    private List<Servico> servicos;

    private String status;

    private Long usuarioId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }// "ONLINE" ou "OFFLINE"

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}