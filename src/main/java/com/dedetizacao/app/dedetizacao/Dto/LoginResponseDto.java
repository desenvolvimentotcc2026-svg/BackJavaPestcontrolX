package com.dedetizacao.app.dedetizacao.Dto;


public class LoginResponseDto {

    private String token;
    private String tipo;
    private Long id;
    private String nome;

    public LoginResponseDto(String token, String tipo, Long id, String nome) {
        this.token = token;
        this.tipo = tipo;
        this.id = id;
        this.nome = nome;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
}