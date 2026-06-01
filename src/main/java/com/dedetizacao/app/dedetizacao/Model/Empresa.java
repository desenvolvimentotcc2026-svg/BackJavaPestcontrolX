package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

@Entity
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    private String senha;
    private String cnpj;
    private String nome;


    @Column(columnDefinition = "TEXT")
    private String sobre;

    @JsonAlias({"mensagem_automatica", "mensagemAutomatica"})
    @Column(name = "mensagem_automatica", columnDefinition = "TEXT")
    private String mensagemAutomatica;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id")
    private EnderecoEmpresa endereco;

    @JsonIgnore
    @OneToMany(mappedBy = "empresa")
    private List<Servico> servicos;

    // --- GETTERS E SETTERS ---
    @Column(name = "chave_corporativa")
    private String chaveCorporativa;

    public String getChaveCorporativa() { return chaveCorporativa; }
    public void setChaveCorporativa(String chaveCorporativa) { this.chaveCorporativa = chaveCorporativa; }

    public Long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobre() { return sobre; }
    public void setSobre(String sobre) { this.sobre = sobre; }

    public String getMensagemAutomatica() { return mensagemAutomatica; }
    public void setMensagemAutomatica(String mensagemAutomatica) { this.mensagemAutomatica = mensagemAutomatica; }

    public EnderecoEmpresa getEndereco() { return endereco; }
    public void setEndereco(EnderecoEmpresa endereco) { this.endereco = endereco; }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }
}