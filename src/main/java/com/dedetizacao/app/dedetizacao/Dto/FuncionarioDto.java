package com.dedetizacao.app.dedetizacao.Dto;

public class FuncionarioDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cargo;
    private Long empresa_id;
    private Boolean ativo;
    private String cpf;

    public FuncionarioDto() {
    }

    public FuncionarioDto(Long id, String nome, String email, String telefone, String cargo, Long empresa_id, Boolean ativo, String cpf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cargo = cargo;
        this.empresa_id = empresa_id;
        this.ativo = ativo;
        this.cpf = cpf;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Long getEmpresa_id() { return empresa_id; }
    public void setEmpresa_id(Long id) { this.empresa_id = id; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}