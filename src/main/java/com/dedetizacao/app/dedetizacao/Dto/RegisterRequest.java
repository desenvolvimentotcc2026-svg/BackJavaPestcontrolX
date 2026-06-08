package com.dedetizacao.app.dedetizacao.Dto;

public class RegisterRequest {

    private String nome;
    private String email;
    private String senha;
    private String tipo;

    private String cnpj;   // usado só empresa/cliente
    private String cep;
    private String rua;
    private String bairro;
    private String numero;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
}