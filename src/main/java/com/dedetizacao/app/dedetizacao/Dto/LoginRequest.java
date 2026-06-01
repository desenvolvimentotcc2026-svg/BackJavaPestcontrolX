package com.dedetizacao.app.dedetizacao.Dto;

public class LoginRequest {
    private String email;
    private String senha;
    private String tipo;
    private String cnpj;

    public LoginRequest() {}

    public LoginRequest(String email, String senha, String tipo, String cnpj) {
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.cnpj = cnpj;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}