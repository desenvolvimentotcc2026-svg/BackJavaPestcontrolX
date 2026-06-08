package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Criando a tabela clientes //
@Entity
@Table(name = "clientes")
public class Cliente {

    private Long empresaId;

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }


    @Id // Criando a chave primária de clientes//
    @GeneratedValue(strategy = GenerationType.IDENTITY) /* coloca a responsabilidade de auto-incrementar o id.*/
    private Long id;

    // Define que muitos clientes pertencem a uma empresa.
    //e o EAGER faz com que a Empresa seja carregada instantaneamente sempre que buscar o Cliente.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id") // Cria a chave estrangeira (FK) no banco com este nome.
    private Empresa empresa;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private EnderecoCliente endereco;

    @Column(nullable = false) // Garante que o campo nunca seja nulo no banco.
    private String nome;

    @Column(nullable = false, unique = true)  // Garante que não existam dois clientes com o mesmo e-mail, telefone,  e endereço.
    private String email;
    private String telefone;





    private String senha;


    @OneToMany(mappedBy = "cliente") // Define o lado inverso da relação, "mappedBy" indica que o controle está no campo 'cliente' da classe Servico.
    @JsonIgnore // Crucial porque Impede que o JSON entre em loop infinito ao tentar listar serviços que listam clientes.
    private List<Servico> servicos;

    private Long usuarioId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Cliente() {}

    public Cliente(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;

    }

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public EnderecoCliente getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCliente endereco) {
        this.endereco = endereco;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}