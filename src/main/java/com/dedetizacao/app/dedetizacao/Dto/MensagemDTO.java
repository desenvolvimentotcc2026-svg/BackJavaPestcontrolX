package com.dedetizacao.app.dedetizacao.Dto;

public class MensagemDTO {
    private String remetente;
    private String texto;

    public MensagemDTO() {}

    public MensagemDTO(String remetente, String texto) {
        this.remetente = remetente;
        this.texto = texto;
    }

    // Getters e Setters
    public String getRemetente() { return remetente; }
    public void setRemetente(String remetente) { this.remetente = remetente; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}