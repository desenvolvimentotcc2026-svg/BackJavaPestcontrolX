package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Alterado para nullable = true para suportar o ID do PestBot sem quebrar Foreign Keys
    @Column(name = "remetente_id", nullable = true)
    private Long remetenteId;

    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Mensagem() {
        this.dataHora = LocalDateTime.now();
    }

    public void setTexto(String texto) {
        this.conteudo = texto;
    }

    // Identificador string amigável para o App Android ("PestBot" ou "Humano")
    @Transient // Não salva no banco de dados
    private String tipoRemetente;

    public String getTipoRemetente() {
        return tipoRemetente;
    }

    public void setTipoRemetente(String tipoRemetente) {
        this.tipoRemetente = tipoRemetente;
    }

    public void setRemetente(String remetente) {
        if ("BOT".equalsIgnoreCase(remetente) || "PestBot".equalsIgnoreCase(remetente)) {
            this.remetenteId = null;
            this.tipoRemetente = "PestBot";
        } else {
            try {
                this.remetenteId = Long.parseLong(remetente);
                this.tipoRemetente = "Humano";
            } catch (NumberFormatException e) {
                this.remetenteId = null; // Trata erro sem forçar ID falso
            }
        }
    }

    // Getters e Setters Padrões
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
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}