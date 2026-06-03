package com.dedetizacao.app.dedetizacao.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long remetenteId;
    private Long destinatarioId;
    private Long empresaId;
    private Long clienteId;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private String enviadoPor; // Mapeia o envio vindo do Android

    private LocalDateTime dataHora;

    // Resolve o erro do ChatController e mantém o mapeamento do app Android
    @Transient // Não cria coluna no banco, mas vai na serialização JSON
    private String tipoRemetente;

    public Mensagem() {
        this.dataHora = LocalDateTime.now();
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

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    // Getters e Setters para o ChatController conseguir compilar
    public String getTipoRemetente() { return tipoRemetente; }
    public void setTipoRemetente(String tipoRemetente) { this.tipoRemetente = tipoRemetente; }
}