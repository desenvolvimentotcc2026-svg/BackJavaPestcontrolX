package com.dedetizacao.app.dedetizacao.Dto;

public class ValidarEmpresaRequest {


    private String cnpj;
    private String chaveCorporativa;

    public ValidarEmpresaRequest();

    public ValidarEmpresaRequest(String cnpj, String chaveCorporativa){
        this.cnpj = cnpj;
        this.chaveCorporativa = chaveCorporativa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getChaveCorporativa() {
        return chaveCorporativa;
    }

    public void setChaveCorporativa(String chaveCorporativa) {
        this.chaveCorporativa = chaveCorporativa;
    }



}
