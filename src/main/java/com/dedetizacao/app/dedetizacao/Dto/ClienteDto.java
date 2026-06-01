package com.dedetizacao.app.dedetizacao.Dto;


public class ClienteDto {

    private Long id;

    private String nome;

    private String telefone;

    private String email;

    private Long empresaId;

    private EnderecoClienteDto endereco;

    public ClienteDto() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Long getEmpresaId() { return empresaId; }

    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public EnderecoClienteDto getEndereco() { return endereco; }

    public void setEndereco(EnderecoClienteDto endereco) { this.endereco = endereco; }
}