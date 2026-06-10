package com.dedetizacao.app.dedetizacao.Dto;

public class ValidarEmpresaRequest {
    private String cnpj;
    private String chaveCorporativa;

    // Construtor vazio padrão
    public ValidarEmpresaRequest() {
    }

    // Certifique-se de que ESTA linha abaixo termine com { } e NÃO com ;
    public ValidarEmpresaRequest(String cnpj, String chaveCorporativa) {
        this.cnpj = cnpj;
        this.chaveCorporativa = chaveCorporativa;
    }

    // Getters e Setters
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getChaveCorporativa() { return chaveCorporativa; }
    public void setChaveCorporativa(String chaveCorporativa) { this.chaveCorporativa = chaveCorporativa; }
}