package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public LocalDateTime getExpiracaoCodigo() {
        return expiracaoCodigo;
    }

    public void setExpiracaoCodigo(LocalDateTime expiracaoCodigo) {
        this.expiracaoCodigo = expiracaoCodigo;
    }

    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getCnpj() { return cnpj; }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Adicione este método para facilitar o acesso no JSON e no Android
    public Long getEmpresaId() {
        return (empresa != null) ? empresa.getId() : null;
    }


    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @Column(unique = true)
    private String email;
    private String senha;
    private String cnpj;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo; // EMPRESA, CLIENTE, FUNCIONARIO

    // Campos de recuperação de senha/verificação
    @Column(name = "codigo_verificacao")
    private String codigoVerificacao;
    @Column(name = "expiracao_codigo")
    private LocalDateTime expiracaoCodigo;


}