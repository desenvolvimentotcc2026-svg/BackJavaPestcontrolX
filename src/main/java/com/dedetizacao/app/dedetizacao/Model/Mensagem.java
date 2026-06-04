package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long empresaId;
    private Long clienteId;       // Ajustado para o ChatController
    private Long remetenteId;
    private Long destinatarioId;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private String enviadoPor;    // Para sincronização com o App Android
    private String tipoRemetente; // Utilizado pelo ChatController / BOT

    private LocalDateTime dataHora; // Utilizado pelo MensagemService

    // Construtor Padrão
    public Mensagem() {
    }

    // Getters e Setters Profissionais (Garantem a compilação limpa)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getRemetenteId() { return remetenteId; }
    public void setRemetenteId(Long remetenteId) { this.remetenteId = remetenteId; }

    public Long getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public String getTipoRemetente() { return tipoRemetente; }
    public void setTipoRemetente(String tipoRemetente) { this.tipoRemetente = tipoRemetente; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}