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

    private Long usuarioId;

    @Column(columnDefinition = "TEXT")
    private String sobre;

    @JsonAlias({"mensagem_automatica", "mensagemAutomatica"})
    @Column(name = "mensagem_automatica", columnDefinition = "TEXT")
    private String mensagemAutomatica;

    private String contatoPlantao;
    private String janelaAtendimento;
    private String licencaSanitaria;
    private String responsavelTecnico;

    @ElementCollection
    @CollectionTable(name = "empresa_especialidades", joinColumns = @JoinColumn(name = "empresa_id"))
    @Column(name = "especialidade")
    private List<String> especialidades;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id")
    private EnderecoEmpresa endereco;

    @JsonIgnore
    @OneToMany(mappedBy = "empresa")
    private List<Servico> servicos;

    @Column(name = "chave_corporativa")
    private String chaveCorporativa;

    // --- GETTERS E SETTERS CORRIGIDOS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getSobre() { return sobre; }
    public void setSobre(String sobre) { this.sobre = sobre; }

    public String getMensagemAutomatica() { return mensagemAutomatica; }
    public void setMensagemAutomatica(String mensagemAutomatica) { this.mensagemAutomatica = mensagemAutomatica; }

    public String getContatoPlantao() { return contatoPlantao; }
    public void setContatoPlantao(String contatoPlantao) { this.contatoPlantao = contatoPlantao; }

    public String getJanelaAtendimento() { return janelaAtendimento; }
    public void setJanelaAtendimento(String janelaAtendimento) { this.janelaAtendimento = janelaAtendimento; }

    public String getLicencaSanitaria() { return licencaSanitaria; }
    public void setLicencaSanitaria(String licencaSanitaria) { this.licencaSanitaria = licencaSanitaria; }

    public String getResponsavelTecnico() { return responsavelTecnico; }
    public void setResponsavelTecnico(String responsavelTecnico) { this.responsavelTecnico = responsavelTecnico; }

    public List<String> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<String> especialidades) { this.especialidades = especialidades; }

    public EnderecoEmpresa getEndereco() { return endereco; }
    public void setEndereco(EnderecoEmpresa endereco) { this.endereco = endereco; }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }

    public String getChaveCorporativa() { return chaveCorporativa; }
    public void setChaveCorporativa(String chaveCorporativa) { this.chaveCorporativa = chaveCorporativa; }
}