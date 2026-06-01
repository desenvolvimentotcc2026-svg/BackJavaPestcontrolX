package com.dedetizacao.app.dedetizacao.Dto;

public class RegistroDto {

    private String email;
    private String senha;
    private String role;

    public RegistroDto() {}

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setRole(String role) {
        this.role = role;
    }
}