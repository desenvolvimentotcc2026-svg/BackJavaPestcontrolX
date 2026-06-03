package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos que batem exatamente com o JSON enviado pelo Android
    private Long remetenteId;
    private Long destinatarioId;
    private Long empresaId;
    private Long clienteId;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private String enviadoPor; // Ex: "CLIENTE", "TECNICO", "BOT"

    private LocalDateTime dataHora;

    // Construtor vazio obrigatório para o JPA
    public Mensagem() {
        this.dataHora = LocalDateTime.now();
    }

    // Getters e Setters Padrões (Jackson usa isso para montar o objeto)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRemetenteId() { return remetenteId; }
    public void setRemetenteId(Long remetenteId) { this.remetenteId = remetenteId; }

    public Long getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}